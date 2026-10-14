# Selorria AutoClicker

**Click less. Get more done.**

A lightweight Android utility for simple repeated tapping. Pick a target on your screen, configure the interval and repeat behavior, then start or stop from a movable floating panel.

## Features
- Tap to select one exact target position.
- Configurable click interval (minimum 50 ms).
- Fixed repeat count or continuous repeat (`0` means continuous).
- Floating Start / Stop controls and an emergency stop.
- No root required; minimum Android 7.0 (API 24).
- No script editor, script import/export, multi-point sequences, or swipe gestures.

## Build and install
1. Open this folder in Android Studio with Android Gradle Plugin 8.6.1 or compatible.
2. Build and run on an Android 7.0+ device.
3. In the app, allow **Display over other apps**.
4. Open Android Accessibility settings and enable **Selorria AutoClicker**.
5. Return to the app and set an interval and repeat count, then launch floating controls.
6. Open the app where you want to tap. In the floating panel, choose **Pick target**, then tap the desired position. Choose **Start** to begin or **Stop** to stop immediately.

## Notes
- Target coordinates are screen pixels from the top-left and can change with orientation, resolution, or app layout.
- Android requires the user to explicitly grant overlay permission and enable the Accessibility service. The app does not bypass these protections.
- The target picker briefly places a transparent touch overlay above the current screen; selecting a target consumes that touch rather than passing it through to the underlying app.
- Use automation only where permitted by the target app or service. Do not automate payments, security prompts, or other sensitive actions.
- This source project has not been built or device-tested in this environment. Validate before distributing it.
