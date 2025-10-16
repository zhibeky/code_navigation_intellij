package com.example.textsearch

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.atomic.AtomicReference
import javax.swing.*

class SearchToolWindowPanel(private val project: Project) : JPanel(BorderLayout()) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private val dirField = JTextField()
    private val queryField = JTextField()
    private val startButton = JButton("Start search")
    private val cancelButton = JButton("Cancel search")
    private val resultsModel = DefaultListModel<String>()
    private val resultsList = JList(resultsModel)

    private val currentJob = AtomicReference<Job?>()

    init {
        val inputPanel = JPanel()
        inputPanel.layout = BoxLayout(inputPanel, BoxLayout.Y_AXIS)

        val dirRow = JPanel(BorderLayout(8, 0))
        dirRow.add(JLabel("Directory path:"), BorderLayout.WEST)
        dirField.toolTipText = "Absolute directory path to search"
        dirRow.add(dirField, BorderLayout.CENTER)

        val queryRow = JPanel(BorderLayout(8, 0))
        queryRow.add(JLabel("Search string:"), BorderLayout.WEST)
        queryField.toolTipText = "Text to search for"
        queryRow.add(queryField, BorderLayout.CENTER)

        val buttonsRow = JPanel()
        buttonsRow.layout = BoxLayout(buttonsRow, BoxLayout.X_AXIS)
        buttonsRow.add(startButton)
        buttonsRow.add(Box.createRigidArea(Dimension(8, 1)))
        buttonsRow.add(cancelButton)

        inputPanel.add(dirRow)
        inputPanel.add(Box.createRigidArea(Dimension(1, 8)))
        inputPanel.add(queryRow)
        inputPanel.add(Box.createRigidArea(Dimension(1, 8)))
        inputPanel.add(buttonsRow)

        val scrollPane = JScrollPane(resultsList)
        add(inputPanel, BorderLayout.NORTH)
        add(scrollPane, BorderLayout.CENTER)

        startButton.addActionListener(this::onStart)
        cancelButton.addActionListener(this::onCancel)
        cancelButton.isEnabled = false
    }

    private fun onStart(@Suppress("UNUSED_PARAMETER") e: ActionEvent) {
        val directory = dirField.text.trim()
        val query = queryField.text
        if (directory.isEmpty() || query.isEmpty()) {
            showError("Please provide both directory and search string.")
            return
        }

        val path = Paths.get(directory)
        if (!Files.isDirectory(path)) {
            showError("Directory does not exist: $directory")
            return
        }

        startButton.isEnabled = false
        cancelButton.isEnabled = true
        dirField.isEnabled = false
        queryField.isEnabled = false
        resultsModel.clear()

        val job = scope.launch {
            try {
                Files.walk(path).use { stream ->
                    stream.filter { Files.isRegularFile(it) }
                        .forEach { file ->
                            if (!isActive) return@forEach
                            processFile(file, query)
                        }
                }
            } finally {
                SwingUtilities.invokeLater {
                    startButton.isEnabled = true
                    cancelButton.isEnabled = false
                    dirField.isEnabled = true
                    queryField.isEnabled = true
                }
                currentJob.set(null)
            }
        }
        currentJob.getAndSet(job)?.cancel()
    }

    private fun onCancel(@Suppress("UNUSED_PARAMETER") e: ActionEvent) {
        cancelButton.isEnabled = false
        val job = currentJob.getAndSet(null) ?: return
        scope.launch {
            job.cancelAndJoin()
            SwingUtilities.invokeLater {
                startButton.isEnabled = true
                dirField.isEnabled = true
                queryField.isEnabled = true
            }
        }
    }

    private fun processFile(file: Path, query: String) {
        // Read as text with platform default; ignore binary errors gracefully
        val content: String = try {
            Files.readString(file, Charset.defaultCharset())
        } catch (_: Exception) {
            return
        }

        var index = content.indexOf(query)
        if (index < 0) return

        val lineBreaks = IntArray(content.length)
        var count = 0
        for (i in content.indices) {
            if (content[i] == '\n') {
                lineBreaks[count++] = i
            }
        }

        while (index >= 0) {
            val lineNumber = 1 + countLineBreaksBefore(lineBreaks, count, index)
            val lineStart = if (lineNumber == 1) 0 else lineBreaks[lineNumber - 2] + 1
            val column = index - lineStart + 1
            val relative = project.basePath?.let { base ->
                val basePath = Paths.get(base)
                try {
                    basePath.relativize(file).toString()
                } catch (_: Exception) {
                    file.toString()
                }
            } ?: file.toString()

            val entry = "$relative: $lineNumber:$column"
            SwingUtilities.invokeLater { resultsModel.addElement(entry) }
            index = content.indexOf(query, index + 1)
        }
    }

    private fun countLineBreaksBefore(lineBreaks: IntArray, count: Int, index: Int): Int {
        // binary search over collected line breaks
        var lo = 0
        var hi = count - 1
        var ans = -1
        while (lo <= hi) {
            val mid = (lo + hi) ushr 1
            val v = lineBreaks[mid]
            if (v < index) {
                ans = mid
                lo = mid + 1
            } else {
                hi = mid - 1
            }
        }
        return ans + 1
    }

    private fun showError(message: String) {
        ApplicationManager.getApplication().invokeLater {
            JOptionPane.showMessageDialog(this, message, "Directory Text Search", JOptionPane.ERROR_MESSAGE)
        }
    }
}


