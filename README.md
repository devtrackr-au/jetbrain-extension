# DevTrackr - JetBrains Plugin

Effortless time tracking for developers using JetBrains IDEs (IntelliJ IDEA, PyCharm, WebStorm, etc.).

## Features

✅ Automatic time tracking for coding sessions  
✅ Detailed insights on project time allocation  
✅ Seamless integration with popular version control platforms  
✅ Real-time productivity analytics and reports  
✅ Lightweight and non-intrusive — no manual timers required

## Installation

### From Source

1. Clone this repository
2. Open the project in IntelliJ IDEA
3. Run the `buildPlugin` Gradle task
4. Install the generated plugin from `build/distributions/devtrackr-jetbrains-1.0.0.zip`

### Development Setup

1. Ensure you have IntelliJ IDEA with the IntelliJ Platform Plugin SDK configured
2. Import the project as a Gradle project
3. Run the `runIde` Gradle task to launch a sandbox IDE instance with the plugin installed

## Configuration

1. Go to `File` → `Settings` → `Tools` → `DevTrackr`
2. Enter your DevTrackr API key
3. Enable debug logging if needed
4. Click `Apply` and `OK`

Alternatively, you can use the menu: `Tools` → `Update API Key`

## Building

```bash
./gradlew buildPlugin
```

The plugin will be built in `build/distributions/`

## Running in Development

### Test with IntelliJ IDEA

```bash
./gradlew runIde
```

This will launch a sandbox IntelliJ IDEA instance with the plugin installed.

### Test with Rider

To test with Rider instead, modify `build.gradle.kts`:

```kotlin
intellij {
    type.set("RD") // Rider
    version.set("2023.3")
}
```

Then run:
```bash
./gradlew runIde
```

This will launch Rider with the plugin installed.

### Install Plugin Manually

1. Build the plugin: `./gradlew buildPlugin`
2. In Rider: `File` → `Settings` → `Plugins` → `⚙️` → `Install Plugin from Disk...`
3. Select `build/distributions/devtrackr-jetbrains-1.0.0.zip`
4. Restart Rider

## Project Structure

```
devtrackr-jetbrains/
├── src/main/kotlin/com/devtrackr/
│   ├── actions/          # Action handlers (menu items, etc.)
│   ├── config/           # Configuration classes
│   ├── services/         # Core services (activity tracking, API)
│   ├── settings/         # Settings UI and persistence
│   ├── types/            # Data types
│   ├── utils/            # Utility classes
│   └── DevTrackrPlugin.kt # Main plugin entry point
├── src/main/resources/
│   └── META-INF/
│       └── plugin.xml    # Plugin manifest
├── build.gradle.kts     # Build configuration
└── README.md
```

## How It Works

1. The plugin automatically tracks your coding activity when you:
   - Open or switch files
   - Make changes to documents
   - Work on projects

2. Activity data is sent to the DevTrackr API at regular intervals (heartbeats)

3. The plugin detects idle periods and stops tracking automatically

4. All time calculations are handled on the server side

## Requirements

- IntelliJ IDEA 2020.3 or later (or compatible JetBrains IDE)
- Java 17 or later (Java 21 LTS recommended)
- Kotlin 1.9.20 or later

### Supported IDE Versions

The plugin supports:
- **Minimum:** IntelliJ Platform 2020.3 (build 203)
- **Maximum:** All future versions (build 999.*)

This means it works with:
- IntelliJ IDEA 2020.3 through latest
- Rider 2020.3 through latest
- PyCharm 2020.3 through latest
- WebStorm 2020.3 through latest
- Other JetBrains IDEs based on IntelliJ Platform 2020.3+

### Setting Up Java

If you don't have Java installed, install Java 21 (LTS) via Homebrew:

```bash
brew install openjdk@21
export JAVA_HOME=/opt/homebrew/opt/openjdk@21
export PATH="$JAVA_HOME/bin:$PATH"
```

Or download from [Adoptium](https://adoptium.net/) or [Oracle](https://www.oracle.com/java/technologies/downloads/).

The `gradle.properties` file is configured to use Java 21. If you have a different Java installation, update the `org.gradle.java.home` path in `gradle.properties`.

## License

MIT

## Publishing

See [PUBLISHING.md](PUBLISHING.md) for detailed instructions on how to publish the plugin to the JetBrains Plugin Repository.

## Support

Visit [devtrackr.com](https://devtrackr.com) for more information and updates.
