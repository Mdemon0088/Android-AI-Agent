# NŪR AI — Android AI Agent

A safety-first, native Android personal AI-agent prototype. The project is deliberately incremental: build a reliable task loop first, then add model access, memory, and narrowly scoped Android actions behind explicit permission and approval gates.

## Current status

**Starter v0.1** — Android/Kotlin/Jetpack Compose app shell and initial CI workflow. The sample screen is local-only. No AI provider is connected, no API key is required, and no AccessibilityService or sensitive Android permission is enabled.

## Build

Requirements: JDK 17, Android SDK 35, and Gradle 8.9.

```bash
gradle assembleDebug
```

In Android Studio, open this repository as a Gradle project and let it sync. A Gradle wrapper will be added after the first reproducible build check.

## Principles

- User control and visible approvals for consequential actions.
- Least privilege: do not request permissions until a feature needs them.
- Secrets stay server-side; never commit API keys or embed them in the APK.
- Model outputs and retrieved web content are untrusted input.
- Verify each action and report success, failure, or uncertainty honestly.
- Store only useful, user-approved memory; allow inspection and deletion.

## Project map

- `app/` — Android app
- `docs/ARCHITECTURE.md` — components and trust boundaries
- `SECURITY.md` — security baseline
- `ROADMAP.md` — staged implementation plan
- `.github/workflows/android.yml` — build CI

## Scope and limitations

This is an early prototype, not an autonomous or security-audited product. It does not control the device, operate other apps, or modify its own model weights. Those capabilities, if added, require explicit design, permissions, testing, and policy review.

## License

No license has been selected yet. Do not assume this project is open for reuse until a license is added.
