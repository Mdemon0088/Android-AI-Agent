# NŪR AI — Android AI Agent

A safety-first, native Android personal AI-agent prototype. The project is incremental: build a reliable task-planning loop first, then add model access, memory, and narrowly scoped Android actions behind explicit permission and approval gates.

## Current status — v0.2.0

- Native Android app built with Kotlin and Jetpack Compose.
- Bangla task input and deterministic plan generation.
- Conservative approval flag for potentially consequential requests.
- Local JUnit tests for planner validation and safety classification.
- GitHub Actions runs the unit tests, builds a debug APK, and uploads the APK as a short-lived artifact.
- The screen and planner are local-only: no AI provider or device automation is connected yet.

## Build

Requirements: JDK 17, Android SDK 35, and Gradle 8.9.

```bash
gradle testDebugUnitTest assembleDebug
```

The debug APK is generated at `app/build/outputs/apk/debug/app-debug.apk`. The GitHub Actions workflow also uploads it as an artifact for seven days when CI succeeds.

## Principles

- User control and visible approvals for consequential actions.
- Least privilege: do not request permissions until a feature needs them.
- Secrets stay server-side; never commit API keys or embed them in the APK.
- Model outputs and retrieved web content are untrusted input.
- Verify each action and report success, failure, or uncertainty honestly.
- Store only useful, user-approved memory; allow inspection and deletion.

## Project map

- `app/` — Android app
- `app/src/main/java/io/nur/aiagent/agent/TaskPlanner.kt` — deterministic local planner
- `app/src/test/` — local unit tests
- `docs/ARCHITECTURE.md` — components and trust boundaries
- `SECURITY.md` — security baseline
- `ROADMAP.md` — staged implementation plan
- `.github/workflows/android.yml` — test, build, and APK-artifact CI

## Scope and limitations

This is an early prototype, not an autonomous or security-audited product. It creates plans but does not execute them, operate other apps, or modify model weights. AI integration and device actions will be introduced separately with reviewable tests and explicit controls.

## License

No license has been selected yet. Public visibility does not by itself grant broad reuse rights; a license should be chosen before inviting external reuse.
