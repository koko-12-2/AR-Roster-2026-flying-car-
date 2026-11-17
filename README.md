# AR Roster 2026 Flying Car

A native Android flying-car game rendered in real 3D with OpenGL ES 2.0. It draws a procedural low-poly city, flyable car, rooftop details, and an on-screen touch HUD. The game runs offline and does not request camera access or require ARCore, so the 3D game mode works on Oppo A38 as well as other Android devices supporting OpenGL ES 2.0.

## Build and install APK

The repository's GitHub Actions workflow builds a debug APK on every push to `main` and can be run manually:

1. Open **Actions** → **Build Android APK**.
2. Select **Run workflow** (or wait for a push build to complete).
3. Download the `ar-roster-flying-car-debug-apk` artifact from the completed run and unzip it.
4. Install `app-debug.apk` on your Android phone. Android may ask permission to install from the browser/file manager.

This is a test/debug APK, not a Play Store-signed release. This is a true 3D game scene, not camera-based AR. Unsupported ARCore devices cannot display AR over the live camera feed.

## Controls

- Drag the lower-left joystick to steer.
- Hold **LIFT** to climb; release to descend.
- Use **LEFT** and **RIGHT** for turns.
- Tap the circular arrow to restart.
