# Architecture

## Current starter

The first milestone is a native Android shell built with Kotlin and Jetpack Compose. It requests no sensitive permissions and has no AI provider or device-control functionality.

## Current local components

1. **Android UI** — Bangla task entry, visible plan, explicit save action, and saved-plan list.
2. **Deterministic planner** — validates task length and flags common potentially consequential actions for approval review.
3. **Local memory store** — stores only plans the user explicitly saves in app-private SQLite. It supports listing and deleting records; it does not transmit them to a server.
4. **Unit tests** — verify the planner's input handling and basic approval-classification behavior.
5. **CI workflow** — runs unit tests, compiles a debug APK, and uploads the APK as a short-lived build artifact.

## Planned components

1. **Agent coordinator** — explicit task states: draft → planned → awaiting approval → running → verifying → completed/failed/cancelled.
2. **Model adapter** — calls a configured model through a server-side proxy and validates structured responses.
3. **Tool registry** — allowlisted, narrowly scoped operations. The model may propose a tool call; app code validates it before execution.
4. **Approval gate** — required for external or consequential actions such as sending messages, changing account settings, exposing personal data, deleting files, or spending money.
5. **Audit log** — user-visible task summaries, tool names, approval results, and errors. Never log API keys or full private content by default.

## Trust boundary

Model output and web content are untrusted input. Neither may override app policy. Tool execution must happen in app-owned code, not through arbitrary model-generated shell commands. Secrets must not be embedded in the APK or committed to the repository.

## Future Android integrations

Use Android intents and official app APIs when available. AccessibilityService is a last-resort, user-enabled integration for eligible UI automation; it must be transparent, narrowly scoped, and designed around Android platform policies. This starter does not implement AccessibilityService.

## First implementation sequence

1. Complete and test the local planner.
2. Add explicit opt-in local persistence.
3. Add a server-side model proxy and structured tool calling.
4. Add one harmless, allowlisted Android action and verification.
5. Add opt-in permissions and additional tools one at a time.
