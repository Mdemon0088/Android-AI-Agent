# NŪR AI — Android AI Agent

A safety-first personal Android AI-agent prototype built incrementally.

## Current status — Android v0.3.0 + API prototype

- Native Android app built with Kotlin and Jetpack Compose.
- Bangla task input and deterministic plan generation.
- Potentially consequential tasks are flagged for user approval.
- Local JUnit tests for planner validation and approval classification.
- Explicit opt-in local memory: saved plans stay in app-private SQLite and can be deleted.
- A separate Node.js 22+ API prototype calls the OpenAI Responses API from the server, validates structured plans, applies a server-side approval check, and never executes device actions.
- CI runs backend tests without credentials, Android unit tests, and a debug APK build. The APK is uploaded as a seven-day workflow artifact.

## Build Android app

Requirements: JDK 17, Android SDK 35, and Gradle 8.9.

Run `gradle testDebugUnitTest assembleDebug` from the project root. The debug APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.

## Test and run the API prototype

Requirements: Node.js 22 or later. The server has no npm runtime dependencies.

Run `node --test server/test/*.test.js` to test without an API key or paid requests. See `server/README.md` for environment variables and run instructions.

The server requires OPENAI_API_KEY, OPENAI_MODEL, and NUR_AGENT_TOKEN to be provided as deployment environment secrets. Do not commit real values. The Android app is not connected to the backend and the backend is not deployed yet.

## Security and privacy

- The Android app does not request sensitive device-control permissions.
- Plans are saved only after the user explicitly presses Save; saved records are local to the app's private SQLite database.
- The API requires bearer-token authentication, caps request sizes and task length, rate-limits requests, and returns generic provider errors.
- OpenAI credentials remain server-side; never embed a provider key in the Android APK.
- Model output and retrieved content are untrusted input.
- The backend only creates plans; it does not execute tool calls or Android actions.

## Project map

- `app/` — native Android app and local tests
- `server/src/app.js` — authenticated HTTP API and model adapter
- `server/src/server.js` — environment-based server startup
- `server/test/` — credential-free backend tests
- `docs/ARCHITECTURE.md` — architecture and trust boundaries
- `SECURITY.md` — security baseline
- `ROADMAP.md` — staged implementation plan
- `.github/workflows/android.yml` — backend tests, Android tests/build, and APK artifact

## Scope and limitations

This is an early prototype, not a security-audited product. The AI backend is not deployed and the Android app is not connected to it. The application does not currently control other apps or the phone, and neither the server nor the local planner executes plans.

## License

No license has been selected yet. Public visibility does not by itself grant broad reuse rights.
