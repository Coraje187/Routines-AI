# Routines AI v1.0.0 — Genesis OS Initial Public Release 🚀

Welcome to the first official public release of **Routines AI**! 

Routines AI is an open-source, subscription-free smart wall calendar, visual routine board, and family memory engine built for busy households and neurodivergent children (ADHD/Autism).

---

## 📦 Downloads & Binaries

| Platform | Download | Description |
| :--- | :--- | :--- |
| **Android (Tablets & Phones)** | [`RoutinesAI-v1.0.0.apk`](#) | Native Android APK for Amazon Fire Max 11, Fire HD 10, and Android 11+ devices. |
| **Windows PC** | [`RoutinesAI-v1.0.0-windows-x64.zip`](#) | Standalone portable Windows desktop application. Unzip and run `Routines AI.exe`. |
| **Linux & Apple macOS** | Source (`desktop/`) | Run via `npm start` or `./start-linux-mac.sh`. |

---

## 🌟 What's New in v1.0.0

### 📅 Full-Month Wall Calendar & Advance Scheduling
- Interactive 7-column calendar grid with month navigation.
- Color-coded event category dots (School, Therapy, Activity, Medical, Family).
- Day agenda drawer displaying schedules, time slots, and assigned kids.
- Advance event scheduler with the `+ Add Event` modal.

### 🧹 Visual Chores & Star Rewards System
- Visual category icons (🛏️ Bed, 🧸 Toys, 🐱 Pet care, 🍽️ Dishes, 🧺 Laundry, 📚 Reading).
- Interactive star reward counter with synthesized completion chime and celebration pop animation.
- Dynamic multi-kid profile switcher.

### 📋 Visual Routines & First-Then Progression
- PECS-inspired visual "First-Then" banner highlighting current and upcoming tasks.
- Morning, afternoon, and bedtime buckets with accommodations for sensory-sensitive routines.
- Checkable progress cards and delete confirmation.

### 📸 Kid Profiles, Avatar Presets & Custom Photos
- Custom photo uploads stored in app sandbox.
- Fun avatar presets (🦁, 🦄, 🚀, 🐱, 🦖, 🐶, ⚽, 🎨).
- Dynamic profile addition and removal.

### ⛅ Live Wall Clock & Local Weather
- Real-time digital clock and date (`2:53 PM • Sun, Oct 11`).
- Zero-key live weather powered by Open-Meteo.
- Customizable area name, coordinates, and Fahrenheit/Celsius toggle.

### 🖼️ Ambient Digital Family Photo Frame
- Seamless ambient digital frame view on the dashboard.
- Full-screen memory gallery tab with auto-advancing slideshow.

### 🧠 Private On-Device AI Family Brain
- Speech and text memory logging for feeding quirks, sensory triggers, and calm-down protocols.
- Powered by Google MediaPipe GenAI / Gemma 2B running locally on-device.
- Fast offline keyword index mode fallback with zero cloud data sharing.

### 🌓 Persistent Low-Stimulation Sensory Theme
- High-contrast / dim sensory theme designed for nighttime kiosks.
- **Hardware Reboot Persistence**: Remembers theme preference across reboots and restarts.

### 💻 Cross-Platform Desktop & PC Showcase
- Complete desktop app for Windows PC, Linux, and Apple macOS.
- One-click launcher scripts (`start-pc.bat` and `start-linux-mac.sh`).
- Windows desktop shortcut generator (`create-desktop-shortcut.bat`).

---

## 🛠️ Verification & Testing
- Tested and verified on **Amazon Fire Max 11** tablet via ADB (`com.familyroutines`).
- Tested and verified on **Windows 11 PC** via Electron desktop runner.
