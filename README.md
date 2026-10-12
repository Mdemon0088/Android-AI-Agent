# NŪR AI — Android AI Agent

A safety-first, native Android personal AI-agent prototype. The project is incremental: build a reliable task-planning loop first, then add model access, memory, and narrowly scoped Android actions behind explicit permission and approval gates.

## Current status — v0.3.0

- Native Android app built with Kotlin and Jetpack Compose.
- Bangla task input and deterministic plan generation.
- Conservative approval flag for potentially consequential requests.
- Local JUnit tests for planner validation and safety classification.
- Explicit opt-in local memory: the user can save a plan to app-private SQLite storage and delete saved records.
- GitHub Actions runs unit tests, builds a debug APK, and uploads the APK as a seven-day artifact.
- The app is local-only: no AI provider or device automation is connected yet.

## Build

Requirements: JDK 17, Android SDK 35, and Gradle 8.9.

Run `gradle testDebugUnitTest assembleDebug` from the project root.

The debug APK is generated at `app/build/outputs/apk/debug/app-debug.apk`. The GitHub Actions workflow also uploads it as an artifact for seven days when CI succeeds.

## Privacy and safety

- Plans are not saved unless the user presses the save button.
- Saved plans remain in the app-private SQLite database. The app does not send them to a cloud service.
- The app manifest requests no sensitive phone-control permissions.
- Secrets stay server-side; never commit API keys or embed them in the APK.
- Model outputs and retrieved web content must be treated as untrusted input.
- This project creates plans but does not execute them.

## Project map

- `app/` — Android app
- `app/src/main/java/io/nur/aiagent/agent/TaskPlanner.kt` — deterministic local planner
- `app/src/main/java/io/nur/aiagent/memory/LocalMemoryStore.kt` — opt-in local SQLite memory
- `app/src/test/` — local unit tests
- `docs/ARCHITECTURE.md` — components and trust boundaries
- `SECURITY.md` — security baseline
- `ROADMAP.md` — staged implementation plan
- `.github/workflows/android.yml` — test, build, and APK-artifact CI

## Scope and limitations

This is an early prototype, not an autonomous or security-audited product. It does not yet call an AI model, operate other apps, control the phone, or modify model weights. Those capabilities require separate design, configuration, permission review, and testing.

## License

No license has been selected yet. Public visibility does not by itself grant broad reuse rights; a license should be chosen before inviting external reuse.
