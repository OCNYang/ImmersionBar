# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

ImmersionBar is an Android library (v3.2.2) for implementing immersive status bar and navigation bar on Android 4.4+ devices. The library provides both Java and Kotlin APIs for controlling status bar color, transparency, dark mode fonts, and handling edge-to-edge layouts including notch/cutout screens.

**✨ Android 15/16 Support**: As of January 2025, the library has been fully adapted for Android 15 (API 35) and prepared for Android 16 (API 36), including Edge-to-Edge enforcement, WindowInsetsController migration, and enhanced WindowInsets API support. See [ANDROID_15_ADAPTATION.md](ANDROID_15_ADAPTATION.md) for details.

## Build Commands

### Basic Build
```bash
./gradlew build
```

### Build Specific Module
```bash
./gradlew :immersionbar:build              # Core library
./gradlew :immersionbar-ktx:build          # Kotlin extensions
./gradlew :immersionbar-components:build   # Fragment helpers (deprecated)
./gradlew :immersionbar-sample:build       # Sample app
```

### Assemble APK
```bash
./gradlew :immersionbar-sample:assembleDebug
./gradlew :immersionbar-sample:assembleRelease
```

### Clean
```bash
./gradlew clean
```

### Run Sample App
The sample app must be built and installed via Android Studio or using:
```bash
./gradlew :immersionbar-sample:installDebug
```

## Project Structure

### Modules

- **immersionbar/** - Core library module containing the main ImmersionBar API
  - `ImmersionBar.java` - Main API class with fluent builder pattern (~3000+ lines)
  - `BarParams.java` - Configuration parameter holder (includes Android 15 fields)
  - `BarConfig.java` - System bar configuration and measurements (Android 15 WindowInsets support)
  - `VersionAdapter.java` - Android version detection utility (NEW in Android 15 adaptation)
  - `OnInsetsChangeListener.java` - WindowInsets change listener interface (NEW in Android 15 adaptation)
  - `RequestManagerRetriever.java` - Fragment-based lifecycle management
  - Supports Activities, Fragments, DialogFragments, and Dialogs
  - Minimum SDK: 14, Target SDK: 31 (compiled), Supports up to API 36 (Android 16)

- **immersionbar-ktx/** - Kotlin extension module
  - `ImmersionBar.kt` - DSL-style Kotlin extensions for cleaner API usage
  - Includes Android 15+ version detection extensions
  - Depends on core `immersionbar` module
  - Provides `immersionBar { }` DSL function

- **immersionbar-components/** - Fragment helpers (deprecated as of 3.x)
  - Base fragment classes for automatic immersion setup
  - Usage discouraged in favor of manual implementation in onResume/onHiddenChanged

- **immersionbar-sample/** - Demo application
  - Demonstrates various use cases: Activities, Fragments, ViewPager, Dialogs, PopupWindows
  - Uses ViewBinding (enabled)
  - Includes examples for fragment frameworks (Fragmentation), keyboard handling, swipe-back
  - Dependencies: ButterKnife, Glide, RxJava2, EventBus, LeakCanary, Material Design

### Key Architecture Patterns

1. **Lifecycle Management via Fragments**: ImmersionBar uses invisible fragments (`RequestBarManagerFragment`, `SupportRequestBarManagerFragment`) attached to activities to manage lifecycle and avoid memory leaks. This is similar to how Glide manages lifecycle.

2. **Builder Pattern**: The main API uses a fluent builder pattern:
   ```java
   ImmersionBar.with(this)
       .statusBarColor(R.color.colorPrimary)
       .navigationBarDarkIcon(true)
       .init();
   ```

3. **Multi-Window Support**: Can handle different immersion configurations per fragment/dialog using tags via `addTag()` and `getTag()`.

4. **Keyboard Conflict Resolution**: `FitsKeyboard.java` handles soft keyboard conflicts with bottom input fields using layout listeners.

5. **OEM Adaptations**: Special handling for different manufacturers:
   - `SpecialBarFontUtils.java` - MIUI, Flyme OS font color handling
   - `OSUtils.java` - Manufacturer detection
   - `NotchUtils.java` - Notch/cutout screen detection for Huawei, Xiaomi, etc.
   - `EMUI3NavigationBarObserver.java` - Special navigation bar observer for EMUI 3.x

6. **Android Version Adaptation**: Multi-path approach for different Android versions:
   - **Android 15+ (API 35)**: WindowInsetsController + Edge-to-Edge mode (enforced)
   - **Android 11-14 (API 30-34)**: WindowInsetsController (recommended)
   - **Android 5-10 (API 21-29)**: SYSTEM_UI_FLAG_* (legacy, with @Deprecated annotations)
   - **Android 4.4 (API 19-20)**: Translucent flags

## Development Notes

### Version Configuration
- Current version: `3.2.2` (defined in `build.gradle` as `ext.immersionbar_version`)
- Kotlin version: `1.9.22` (upgraded from 1.4.32 for Android 15 support)
- Gradle plugin: `8.2.2` (upgraded from 7.1.2 for Android 15 support)
- Gradle wrapper: `8.5`
- CompileSdk: `31` (uses hardcoded constants for API 33+)

### Android 15/16 Adaptation Status
✅ **Completed High-Priority Tasks**:
1. Version detection utility (`VersionAdapter.java`)
2. WindowInsets change listener (`OnInsetsChangeListener.java`)
3. BarParams Android 15 fields (edgeToEdgeEnabled, debug flags)
4. BarConfig WindowInsets API support
5. ImmersionBar Android 15 adaptation methods
6. Edge-to-Edge compatibility API
7. Kotlin extension updates
8. SYSTEM_UI_FLAG deprecation annotations

⏳ **Pending Tasks**:
- Runtime testing on Android 15 emulator/device
- Gesture navigation detection enhancements
- Documentation updates for public release

📚 **Documentation**:
- [ANDROID_15_ADAPTATION.md](ANDROID_15_ADAPTATION.md) - Technical adaptation details
- [ANDROID_15_EXAMPLES.md](ANDROID_15_EXAMPLES.md) - Usage examples and migration guide

### Publishing
The library is published to Maven Central using the configuration in `gradle/publish-mavencentral.gradle`. Publishing credentials are stored in `local.properties` (not committed to git).

### Chinese Repository Mirrors
The build uses Aliyun mirrors for faster dependency resolution in China:
- https://maven.aliyun.com/repository/central
- https://maven.aliyun.com/repository/public

### Fragment Usage Patterns
When implementing immersion in Fragments:
1. **With ViewPager2**: Implement in `onResume()` when using `BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT`
2. **With show/hide**: Implement in both `onResume()` and `onHiddenChanged(hidden == false)`
3. **With ViewPager**: Use `addOnPageChangeListener` in Activity to switch configurations
4. **With Fragmentation library**: See `FragmentFiveActivity` and `BaseFiveFragment` in sample

### Layout Overlap Solutions
Six methods to handle status bar overlap with layouts (described in README):
1. Custom dimens (not recommended)
2. `fitsSystemWindows` XML attribute (pure colors only)
3. `ImmersionBar.fitsSystemWindows(true)` (pure colors only)
4. `ImmersionBar.statusBarView(view)` (supports gradients, swipe-back)
5. `ImmersionBar.titleBar(view)` using paddingTop (supports gradients, swipe-back)
6. `ImmersionBar.titleBarMarginTop(view)` using marginTop (pure colors only)

**⚠️ Android 15+ Edge-to-Edge Note**: For Android 15+, use `setOnInsetsChangeListener()` instead of the above methods for proper Edge-to-Edge support. See [ANDROID_15_EXAMPLES.md](ANDROID_15_EXAMPLES.md).

### Proguard
Version 3.1.1+ requires no proguard rules. Earlier versions need:
```
-keep class com.gyf.immersionbar.* {*;}
-dontwarn com.gyf.immersionbar.**
```

## Common Gotchas

- Always call `.init()` to apply configuration
- ImmersionBar must be initialized after `setContentView()`
- When using Dialogs (not DialogFragment), must call `ImmersionBar.destroy(this, dialog)` when closing
- Fragment components (immersionbar-components) are deprecated; use manual lifecycle methods
- Keyboard enable mode may conflict with certain layouts; test with `keyboardEnable(true)`
- For auto dark mode, must specify status/navigation bar colors first
- **Android 15+**: Don't use `fitsSystemWindows="true"` in XML when using Edge-to-Edge mode; handle insets programmatically via `setOnInsetsChangeListener()`

## Android 15+ Specific Notes

### Edge-to-Edge Mode
Android 15 (API 35) enforces Edge-to-Edge for apps with targetSdk 35+. The library automatically detects and handles this:
```java
// Automatically uses Edge-to-Edge on Android 15+
ImmersionBar.with(this)
    .statusBarColor(Color.TRANSPARENT)
    .setOnInsetsChangeListener((top, bottom, left, right) -> {
        // Handle insets changes
        view.setPadding(left, top, right, bottom);
    })
    .init();
```

### Version Detection
Use `VersionAdapter` for version-specific logic:
```java
if (VersionAdapter.isAndroid15OrAbove()) {
    // Android 15+ specific code
}
```

Kotlin extensions:
```kotlin
if (isAndroid15OrAbove) {
    // Android 15+ specific code
}
```

### Debug Mode
Enable debug logging to verify which mode is active:
```java
ImmersionBar.with(this)
    .debugPrintVersionInfo(true)
    .init();
// Check logcat: "ImmersionBar: Android 15+ Edge-to-Edge mode: ..."
```