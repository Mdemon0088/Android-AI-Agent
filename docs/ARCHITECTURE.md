# Architecture

## Current Android components

1. **Android UI** — Bangla task entry, visible plan, explicit save action, and saved-plan list.
2. **Deterministic planner** — validates task length and flags common potentially consequential actions for approval review.
3. **Local memory store** — saves only user-selected plans in app-private SQLite; records can be listed and deleted.
4. **Android CI** — runs local JUnit tests, compiles a debug APK, and publishes a short-lived artifact.

## Current API components

1. **HTTP server** — Node.js built-in HTTP server, no runtime npm dependencies.
2. **Authentication** — constant-time comparison for the configured bearer token.
3. **Input boundary** — 8 KiB JSON body limit and 1000-character task limit.
4. **Rate limiter** — simple in-memory per-IP window for the single-user prototype.
5. **Model adapter** — calls the OpenAI Responses API with strict JSON Schema output; provider key remains server-side.
6. **Safety normalization** — validates the model plan and applies a deterministic server-side check for common sensitive-action indicators.
7. **Backend CI tests** — fake upstream provider; tests do not require credentials or make paid API requests.

## Trust boundaries

- Model output and web content are untrusted input. Neither may override app policy.
- The backend creates plans only; it does not execute tools or Android actions.
- Secrets must not be embedded in the APK or committed to Git.
- Task input is sent to the AI provider only when a user configures and calls the backend. The Android app is not connected to it yet.

## Future components

1. **Android API client** — user-configurable HTTPS base URL and authentication token, with connect/test and clear error states.
2. **Agent coordinator** — task states: draft → planned → awaiting approval → running → verifying → completed/failed/cancelled.
3. **Tool registry** — allowlisted, narrowly scoped operations; validate every proposed operation before execution.
4. **Approval gate** — explicitly confirm external or consequential actions.
5. **Memory retrieval** — user-visible, opt-in selection of relevant local notes to provide context for a task.
6. **Action verifier** — compare expected effects to actual results and report uncertainty honestly.

## Future Android integrations

Use Android intents and official app APIs when available. AccessibilityService is a last-resort, user-enabled integration for eligible UI automation; it must be transparent, narrowly scoped, and designed around Android platform policies.
