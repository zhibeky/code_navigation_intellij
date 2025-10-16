plugins {
    kotlin("jvm") version "2.0.0"
    id("org.jetbrains.intellij") version "1.17.3"
}

repositories {
    mavenCentral()
}

dependencies {
}

intellij {
    version.set("2024.2")
    type.set("IC")
    plugins.set(listOf())
}

tasks {
    patchPluginXml {
        sinceBuild.set("242")
        untilBuild.set(null as String?)
    }
}

