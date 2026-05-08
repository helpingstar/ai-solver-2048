# AI Solver 2048

AI Solver 2048 is an Android 2048 workspace that helps players inspect a board, run on-device move analysis, and optionally let the app execute recommended moves automatically.

<a href='https://play.google.com/store/apps/details?id=io.github.helpigstar.coach2048'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png' height='80px'/></a>

Built with:

- Kotlin
- Jetpack Compose
- Hilt
- AndroidX Navigation
- Kotlin Coroutines
- Kotlin Serialization
- Google AI Edge LiteRT
- Local `.tflite` policy model for on-device inference

## Demo

https://github.com/user-attachments/assets/5994189b-12d9-4580-8f4d-c43ff70ab431

## Features

- Editable 4x4 2048 board for setting up any game state.
- Manual swipe controls with standard 2048 merge and score behavior.
- On-device AI recommendations for the four move directions.
- Auto-analyze mode that refreshes recommendations as the board changes.
- Auto-move mode that repeatedly applies the top recommendation.
- Undo, reset, optional random tile spawning, and animation controls.
- First-run onboarding and persisted workspace settings.

## How to Use

1. Launch the app and complete the onboarding flow.
2. Tap a board cell to enter or change a tile value.
3. Swipe the board to play manually, or tap a recommendation to apply that move.
4. Tap **Analyze** to generate AI move recommendations for the current board.
5. Tap **Auto** to let the app repeatedly analyze and apply recommended moves.
6. Use the top controls to undo, reset, or open settings for spawn tiles, auto-analysis, and animations.

## Architecture

This project uses a modular Android architecture inspired by the public [Bitwarden Android](https://github.com/bitwarden/android) repository. The main influence is the clear separation between application wiring, shared core code, reusable UI, and data boundaries.

Current module layout:

```text
.
|-- app/      Android application, feature screens, navigation, app-scoped data, and LiteRT inference
|-- core/     Shared models, utilities, and core dependency wiring
|-- data/     Shared disk data-source primitives and preference infrastructure
|-- ui/       Reusable Compose components, theme resources, fonts, and UI helpers
`-- gradle/   Gradle wrapper and version catalog
```

Key patterns:

- **Unidirectional UI flow**: screens send actions to `BaseViewModel`; view models expose immutable state flows and optional one-shot events.
- **Feature-owned state**: the workspace feature keeps board state, undo history, animation phases, recommendations, and auto-move coordination in `WorkspaceViewModel`.
- **Data boundaries**: repositories hide persistence details, while disk sources encapsulate `SharedPreferences` access.
- **Dependency injection**: Hilt modules bind repositories, disk sources, managers, and inference runners.
- **Inference boundary**: `WorkspaceInferenceRunner` isolates LiteRT model execution from game logic.
- **Reusable design layer**: the `ui` module owns board, tile, action, recommendation, bottom-sheet, dialog, theme, and resource components.

## AI Model

The model file lives at:

```text
app/src/main/assets/game2048_policy_value_float32.tflite
```

`LiteRtWorkspaceInferenceRunner` loads the model with Google AI Edge LiteRT, normalizes the 16 board cells, and reads policy logits for the four move directions. `WorkspaceManagerImpl` masks illegal moves, applies softmax over legal directions, and returns sorted confidence percentages for the UI.

## Requirements

- Android Studio with Android Gradle Plugin support.
- JDK 21.
- Android SDK:
  - Compile SDK: 36
  - Target SDK: 35
  - Minimum SDK: 29 for the application
- Gradle wrapper: 9.3.1
