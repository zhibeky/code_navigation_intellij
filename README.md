# 🧭 Directory Text Search 

An IntelliJ IDEA plugin that searches for a given string across all files in a specified directory — built with **Kotlin**, **Swing**, and **coroutines**.  
Implements a responsive tool window that streams search results live as they’re found.

---

## 🚀 Features

- 🔍 **Custom directory input** — type any absolute path (e.g. `/Users/zhibek/Projects/TestApp`)
- ⚡ **Responsive UI** — runs the search in background threads using Kotlin coroutines
- 📄 **Real-time results** — matches appear immediately as they’re discovered
- ⏹️ **Cancelable tasks** — stop a search anytime with one click
- 💬 **Friendly UX** — input validation, tooltips, and clear button states

---

## 🧠 How It Works

1. The plugin adds a **Directory Search** tool window on the right-hand side of IntelliJ.
2. Enter:
   - A directory path (absolute)
   - A text string to search for
3. Press **Start Search**:
   - A coroutine recursively scans all files in that directory.
   - Each occurrence of the string is reported immediately to the UI.
4. Press **Cancel Search**:
   - The coroutine job is canceled gracefully.
   - The UI stays responsive throughout.

Results are shown in the format:

## 🧩 Project Structure

```
CODE_NAVIGATION_INTELLIJ/
│
├── intellij-plugin/
│   ├── src/
│   │   └── main/
│   │       ├── kotlin/com/example/textsearch/
│   │       │   ├── SearchToolWindowFactory.kt   # Registers the ToolWindow
│   │       │   └── SearchToolWindowPanel.kt     # Handles UI + coroutine-based search
│   │       └── resources/META-INF/plugin.xml     # IntelliJ plugin configuration
│   │
│   └── build.gradle.kts                         # Gradle build setup for the plugin
│
├── gradle.properties
├── gradlew / gradlew.bat                        # Gradle wrappers
├── settings.gradle.kts
└── README.md
```
---

## ⚙️ Build & Run

### Prerequisites
- IntelliJ IDEA (Community or Ultimate)
- JDK 17+
- Gradle (or use the wrapper)

### Run the Plugin
```bash
cd intellij-plugin
./gradlew runIde

This opens a sandboxed IntelliJ instance with your plugin loaded.

Build the Plugin
cd intellij-plugin
./gradlew buildPlugin


The packaged .zip will appear under:

intellij-plugin/build/distributions/


You can then install it manually in IntelliJ via
Settings → Plugins → ⚙️ → Install Plugin from Disk...

🧰 Tech Stack
| Component | Technology               |
| --------- | ------------------------ |
| Language  | Kotlin                   |
| UI        | Swing                    |
| Async     | Kotlin Coroutines        |
| IDE SDK   | IntelliJ Platform 2024.2 |

🪄 Implementation Notes

Uses Dispatchers.Default for background searches and SwingUtilities.invokeLater for UI updates.

Skips unreadable/binary files gracefully.

Tracks line and column numbers using newline offsets.

Keeps UI reactive at all times with proper coroutine cancellation.