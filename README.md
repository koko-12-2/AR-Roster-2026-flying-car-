# AR Roster 2026 Flying Car

A lightweight flying-car game that runs in a mobile browser without ARCore. On phones that do not support ARCore (including Oppo A38), use **Game** mode: it renders a simulated 3D-style city scene and does not request camera or AR permissions.

## Run

Open `index.html` in a modern mobile or desktop browser. For a hosted version, enable GitHub Pages for this repository and publish from the `main` branch, root folder.

## Controls

- Touch and drag the steering pad, or use arrow keys / WASD to steer.
- Hold **LIFT** (or Space / Up) to rise.
- Use **LEFT** and **RIGHT** for quick turns.
- Tap **AR / Game** to switch modes. AR is shown as unavailable when no AR runtime is integrated; Game mode always works without ARCore.
- Tap the circular arrow to restart.

This browser game is an ARCore-free fallback, not a way to add ARCore support to unsupported hardware. Actual camera-based AR requires a supported AR runtime and device.
