# iOS Setup Guide for Kotlin Multiplatform Projects

This guide documents the configuration required to connect an iOS app to a Kotlin Multiplatform (KMP) shared framework.

## Prerequisites

- Xcode installed (with command line tools)
- Java JDK 17+ installed (via Homebrew: `brew install openjdk@17`)
- Gradle wrapper in project root

## Project Structure

```
project-root/
├── composeApp/
│   ├── build.gradle.kts          # KMP module config
│   └── src/
│       ├── commonMain/           # Shared Kotlin code
│       ├── androidMain/          # Android-specific code
│       └── iosMain/              # iOS-specific code
│           └── kotlin/.../MainViewController.kt
├── iosApp/
│   ├── iosApp.xcodeproj/
│   │   └── project.pbxproj       # Xcode project file
│   ├── iosApp/
│   │   ├── ContentView.swift     # SwiftUI entry point
│   │   └── Info.plist
│   └── Configuration/
│       └── Config.xcconfig       # Build configuration
└── gradlew                       # Must be executable
```

## Step 1: Gradle Configuration (build.gradle.kts)

Configure the iOS framework in your KMP module:

```kotlin
kotlin {
    listOf(
        iosArm64(),           // Physical devices
        iosSimulatorArm64()   // Apple Silicon simulators
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "YourFrameworkName"  // e.g., "ToucanKMP"
            isStatic = true
        }
    }
}
```

## Step 2: iOS Entry Point (MainViewController.kt)

Create the Compose UI entry point in `iosMain`:

```kotlin
// composeApp/src/iosMain/kotlin/com/yourpackage/MainViewController.kt
package com.yourpackage

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController { App() }
```

## Step 3: Xcode Configuration (Config.xcconfig)

Create/update `iosApp/Configuration/Config.xcconfig`:

```xcconfig
TEAM_ID=YOUR_TEAM_ID

PRODUCT_NAME=YourApp
PRODUCT_BUNDLE_IDENTIFIER=com.yourcompany.yourapp

CURRENT_PROJECT_VERSION=1
MARKETING_VERSION=1.0

// Java path for Gradle builds (required if Java not in system PATH)
JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home

// Framework search path - where Xcode finds the compiled KMP framework
FRAMEWORK_SEARCH_PATHS=$(inherited) $(SRCROOT)/../composeApp/build/bin/iosSimulatorArm64/debugFramework
```

**Note:** Adjust `JAVA_HOME` based on your Java installation:
- Homebrew (Apple Silicon): `/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home`
- Homebrew (Intel): `/usr/local/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home`
- Manual install: Check with `/usr/libexec/java_home`

## Step 4: Xcode Build Phase Script

In `project.pbxproj`, ensure the "Compile Kotlin Framework" build phase includes Java in PATH:

```bash
if [ "YES" = "$OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED" ]; then
  echo "Skipping Gradle build task invocation due to OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED environment variable set to \"YES\""
  exit 0
fi
export JAVA_HOME="$JAVA_HOME"
export PATH="$JAVA_HOME/bin:$PATH"
cd "$SRCROOT/.."
./gradlew :composeApp:embedAndSignAppleFrameworkForXcode
```

## Step 5: SwiftUI Integration (ContentView.swift)

```swift
import UIKit
import SwiftUI
import YourFrameworkName  // e.g., ToucanKMP

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea()
    }
}
```

## Step 6: Initial Build

Before opening in Xcode, build the framework from terminal:

```bash
# Make gradlew executable (one-time)
chmod +x ./gradlew

# Build the framework
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
```

## Troubleshooting

### "Unable to locate a Java Runtime"
- Ensure `JAVA_HOME` is set correctly in `Config.xcconfig`
- Ensure the build phase script exports `JAVA_HOME` and adds it to `PATH`

### "No such module 'YourFrameworkName'"
- Verify `FRAMEWORK_SEARCH_PATHS` points to the correct build output directory
- Run `./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64` manually
- Clean Xcode build folder (Cmd+Shift+K) and rebuild

### "xcrun: error" or Xcode command line tools issues
```bash
# Point to full Xcode installation
sudo xcode-select -s /Applications/Xcode.app/Contents/Developer

# Accept license
sudo xcodebuild -license accept
```

### Navigation or library compatibility issues
- Check Kotlin version compatibility with libraries
- For `navigation-compose`, ensure version matches Kotlin version
- Use stable versions when possible (alpha/beta may have compatibility issues)

## Framework Search Paths

The framework output location depends on the build configuration:

| Configuration | Path |
|--------------|------|
| Debug Simulator (arm64) | `build/bin/iosSimulatorArm64/debugFramework/` |
| Release Simulator (arm64) | `build/bin/iosSimulatorArm64/releaseFramework/` |
| Debug Device (arm64) | `build/bin/iosArm64/debugFramework/` |
| Release Device (arm64) | `build/bin/iosArm64/releaseFramework/` |

For dynamic configuration, you can use:
```xcconfig
FRAMEWORK_SEARCH_PATHS=$(inherited) $(SRCROOT)/../composeApp/build/xcode-frameworks/$(CONFIGURATION)/$(SDK_NAME)
```

This requires the `embedAndSignAppleFrameworkForXcode` task to properly set up the xcode-frameworks directory.

## Version Compatibility Notes

As of late 2025:
- Kotlin 2.2.x works with navigation-compose 2.9.x
- Compose Multiplatform 1.9.x requires Kotlin 2.1+
- Always check JetBrains compatibility matrix for library versions
