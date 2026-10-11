# Routines AI ⚡

[![Android](https://img.shields.io/badge/Platform-Android_11+_|_Fire_OS_8-brightgreen.svg)](https://developer.android.com)
[![Compose](https://img.shields.io/badge/UI-Jetpack_Compose-blue.svg)](https://developer.android.com/jetpack/compose)
[![AI](https://img.shields.io/badge/AI-Gemma_2B_|_MediaPipe_GenAI-purple.svg)](https://ai.google.dev/gemma)
[![License](https://img.shields.io/badge/License-MIT-orange.svg)](LICENSE)
[![Zero-Subscription](https://img.shields.io/badge/Subscription-100%25_Free_&_Offline-success.svg)](#)

> **Routines AI** is an open-source, subscription-free Android kiosk wall calendar, visual routine board, and shared family memory engine built for neurodivergent households and busy families. Powered by local on-device AI (Google Gemma 2 2B) and Genesis OS design primitives.

---

## 🌟 Key Features

### 📅 1. Full Month Wall Calendar & Advance Scheduling
- **Interactive Month Grid**: 7-column calendar grid (Sunday through Saturday) showing dates, current day indicator ("TODAY"), and advance appointments.
- **Visual Event Chips**: Color-coded indicator pills for each child or whole family directly on the calendar days.
- **Day Agenda Drawer**: Tap any day to view scheduled activities, time slots, assigned kid, and category.
- **Advance Event Scheduling**: Easily schedule upcoming doctor appointments, therapy sessions, school events, and family outings in advance using the `+ Add Event` dialog.
- **Categories**: 🏫 School, 🧩 Therapy, ⚽ Activity, 🏥 Medical, 🏠 Family.

### 🧹 2. Visual Chores & Star Rewards System
- **Category Icons**: Visual iconography for quick comprehension (🛏️ Bed, 🧸 Toys, 🐱 Pet care, 🍽️ Dishes, 🧺 Laundry, 📚 Reading).
- **Point & Star Tracking**: Children earn points/stars upon completing chores and routines.
- **Interactive Multi-Kid Selector**: Switch between profiles (Leo, Maya, etc.) or add new children dynamically via the `+ Add Kid` dialog.

### 📋 3. Visual Routines & First-Then Progression
- **PECS-Inspired Structure**: Clear visual "First-Then" progression card that highlights what to do now and what comes next.
- **Customizable Routine Items**: Morning, afternoon, and bedtime buckets with sensory accommodations (e.g., comfortable socks, dim lights, bubblegum toothpaste).

### 🌓 4. Sensory Dim / High-Contrast Dark Theme
- One-tap toggle for low-stimulation dark mode, essential for sensory-sensitive children and nighttime wall kiosk display.

### 🧠 5. Routines AI Brain (Local Family Memory Engine)
- **Shared Parent Knowledge**: Captures feeding quirks, sensory triggers, and calm-down protocols logged by parents via speech-to-text or typed notes.
- **On-Device Inference**: Powered by **Gemma 2 2B** via Google MediaPipe Tasks GenAI running entirely on-device (zero cloud subscription, complete privacy).
- **Graceful Fallback**: Automatically operates in high-speed **Keyword Index Mode** if LLM weights are not loaded.

### 📸 6. Kid Photos & Avatar Presets
- **Custom Photo Upload**: Pick photos from the tablet gallery or camera roll to set as profile pictures next to each child's name.
- **Permanent Local Storage**: Photos are copied directly into the app's internal sandbox, ensuring fast offline loading and zero permission loss across tablet reboots.
- **Fun Avatar Presets**: Choose from playful emoji avatars (🦁, 🦄, 🚀, 🐱, 🦖, etc.) if you don't have a photo ready.

### ⛅ 7. Live Wall Clock & Local Weather
- **Live Digital Clock & Date**: Large, high-visibility digital wall clock updating in real-time (`2:24 PM • Sun, Oct 11`).
- **Zero-Key Local Weather**: Displays current temperature, weather conditions, and location using Open-Meteo.
- **Customizable Area**: Tap the weather badge to easily change your city/area and toggle between Fahrenheit (°F) and Celsius (°C).

### 🖼️ 8. Digital Family Photo Frame
- **Ambient Kiosk Photo Display**: Switch the right dashboard pane from AI Brain to an ambient digital photo frame that displays family memories on the wall all day.
- **Full-Size Gallery Tab**: Dedicated `🖼️ Family Frame` navigation tab for full-resolution photos, captions, and thumbnails.
- **Auto-Advancing Slideshow**: Optional smooth auto-rotating slideshow for revolving family vacation and milestone memories.

---

## 🛠️ Architecture & Tech Stack

```
family-routines-app/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/familyroutines/
│   │   │   ├── MainActivity.kt               # Kiosk lifecycle, immersive mode & dialogs
│   │   │   ├── FamilyApplication.kt          # App singletons & memory engine init
│   │   │   ├── ai/
│   │   │   │   ├── FamilyMemoryEngine.kt     # MediaPipe GenAI / Gemma 2B runner
│   │   │   │   └── PromptBuilder.kt          # Grounded assistant prompt template
│   │   │   ├── data/
│   │   │   │   ├── local/
│   │   │   │   │   ├── FamilyDatabase.kt     # Room SQLite Database (Version 3)
│   │   │   │   │   ├── FamilyDao.kt          # Reactive Room DAOs (Flows)
│   │   │   │   │   └── entities/             # Child, Routine, Chore, Calendar, Note
│   │   │   │   └── repository/
│   │   │   │       └── FamilyRepository.kt   # Clean repository layer
│   │   │   ├── ui/
│   │   │   │   ├── components/
│   │   │   │   │   ├── KidProfileBar.kt      # Genesis OS badge & profile switcher
│   │   │   │   │   ├── MonthCalendarView.kt  # Full 7xN month grid & day agenda
│   │   │   │   │   ├── ChoreItemCard.kt      # Visual chore cards with star values
│   │   │   │   │   ├── RoutineItemCard.kt    # Checkable routine items
│   │   │   │   │   ├── FirstThenBoard.kt     # PECS First/Then progression card
│   │   │   │   │   ├── AddEventDialog.kt     # Advance event scheduling modal
│   │   │   │   │   ├── AddChoreDialog.kt     # Custom chore creator modal
│   │   │   │   │   ├── AddKidDialog.kt       # Profile creator modal
│   │   │   │   │   └── VoiceNoteDialog.kt    # Speech-to-text voice logging modal
│   │   │   │   ├── screens/
│   │   │   │   │   └── KioskDashboardScreen.kt# 2-pane 11-inch landscape kiosk layout
│   │   │   │   └── viewmodel/
│   │   │   │       └── KioskViewModel.kt     # State management & reactive streams
│   │   │   └── voice/
│   │   │       └── ParentVoiceListener.kt    # Android SpeechRecognizer integration
│   │   └── res/
│   │       ├── mipmap-*/                     # Genesis OS Cyber-Lattice Launcher Icons
│   │       └── values/strings.xml
│   └── build.gradle.kts
├── build.gradle.kts
└── settings.gradle.kts
```

- **UI**: Jetpack Compose (BOM 2024.02.00) with Material 3
- **Database**: Room Database 2.6.1 with Kotlin Coroutines & `Flow`
- **AI / LLM**: MediaPipe Tasks GenAI (`com.google.mediapipe:tasks-genai:0.10.27`)
- **Speech**: Android `SpeechRecognizer` with `RECORD_AUDIO` permission

---

## 🚀 Getting Started

### Prerequisites
- **Android SDK**: API 26 (minSdk) to API 34 (compileSdk/targetSdk)
- **JDK**: Java 17 or Java 21
- **Gradle**: 8.4+

### Build & Install

1. **Clone the repository:**
   ```bash
   git clone https://github.com/Coraje187/Routines-AI.git
   cd Routines-AI
   ```

2. **Assemble the Debug APK:**
   ```bash
   ./gradlew assembleDebug
   ```

3. **Install to your connected tablet via ADB:**
   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

4. **Launch the application:**
   ```bash
   adb shell am start -n com.familyroutines/.MainActivity
   ```

---

## 🤖 (Optional) Loading Gemma 2B Local LLM

Routines AI runs completely offline without needing cloud API keys. To enable local Gemma 2 2B LLM inference:

1. Download `gemma-2b-it-cpu-int4.bin` or `gemma-2b-it-gpu-int4.bin` from [Kaggle Models](https://www.kaggle.com/models/google/gemma/tfLite) or HuggingFace.
2. Push the weights into the app's internal sandbox via ADB:
   ```bash
   adb push gemma-2b-it.bin /data/local/tmp/
   adb shell run-as com.familyroutines cp /data/local/tmp/gemma-2b-it.bin files/
   ```
3. Restart Routines AI — the header indicator will switch to **"Gemma 2B Ready (GPU)"**!

---

## 📺 Tablet Kiosk Setup Tips

For wall-mounted setups (such as the **Amazon Fire Max 11** or **Fire HD 10**):
- **Immersive Mode**: Routines AI automatically activates Android transient sticky immersive mode, concealing the status bar and navigation bars.
- **Display Timeout**: Under Tablet Settings -> Display -> Sleep, select "Never" or activate "Stay Awake when charging" in Developer Options.
- **Home Launcher**: Routines AI declares `CATEGORY_HOME` in `AndroidManifest.xml`, allowing it to serve as the default tablet launcher for single-purpose kiosks.

---

## 📄 License
This project is open-source under the [MIT License](LICENSE).
