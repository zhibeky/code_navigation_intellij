# Kotlin Project

A simple Kotlin project set up with Gradle build system.

## Prerequisites

- Java 17 or higher (JDK)
- Gradle (included via wrapper)

## Project Structure

```
.
├── build.gradle.kts          # Gradle build configuration
├── settings.gradle.kts       # Gradle settings
├── gradle.properties         # Gradle properties
├── gradlew                   # Gradle wrapper script
├── gradlew.bat              # Gradle wrapper script for Windows
├── gradle/                   # Gradle wrapper files
├── src/
│   ├── main/
│   │   └── kotlin/
│   │       └── Main.kt       # Main application file
│   └── test/
│       └── kotlin/
│           └── MainTest.kt   # Test files
└── README.md                 # This file
```

## Getting Started

### Running the Application

```bash
./gradlew run
```

### Building the Project

```bash
./gradlew build
```

### Running Tests

```bash
./gradlew test
```

### Cleaning the Project

```bash
./gradlew clean
```

## Features Demonstrated

The sample code in `Main.kt` demonstrates:

- Basic Kotlin syntax
- String interpolation
- Collections and higher-order functions
- Null safety
- Type inference

## Dependencies

- Kotlin Standard Library
- JUnit 5 for testing

## IDE Support

This project is compatible with:
- IntelliJ IDEA
- Android Studio
- Visual Studio Code (with Kotlin extension)
- Any IDE that supports Gradle projects
