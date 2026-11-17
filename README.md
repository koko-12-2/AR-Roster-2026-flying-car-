# AR Roster 2026 Flying Car

A lightweight flying-car game that runs in a mobile browser without ARCore. On phones that do not support ARCore (including Oppo A38), use **Game** mode: it renders a simulated 3D-style city scene and does not request camera or AR permissions.

## Installable Android APK

This repository includes a native Android wrapper for the offline game. It runs the existing `index.html` in a full-screen Android WebView; the game itself does not need internet, camera access, or ARCore.

1. Open the repository's **Actions** tab and select **Build Android APK**.
2. Run the workflow with **Run workflow**, or wait for it to finish after a commit.
3. Open the completed workflow run, download the `ar-roster-flying-car-debug-apk` artifact, and unzip it.
4. Install `app-debug.apk` on the phone. Android may ask you to allow installation from the browser or file manager used to open it.

The APK is a debug build for testing and direct installation, not a Play Store release. A Play Store release needs a separately configured signing key.

## Controls

- Touch and drag the steering pad, or use arrow keys / WASD to steer.
- Hold **LIFT** (or Space / Up) to rise.
- Use **LEFT** and **RIGHT** for quick turns.
- The game runs without ARCore. Camera-based AR is not available on unsupported hardware.
- Tap the circular arrow to restart.

Actual camera-based AR requires a supported AR runtime and device; this app does not bypass that hardware/software requirement.
