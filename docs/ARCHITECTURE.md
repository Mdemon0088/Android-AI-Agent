# Architecture

## Current starter

The first milestone is a native Android shell built with Kotlin and Jetpack Compose. It requests no sensitive permissions and has no network or device-control functionality.

## Planned components

1. **Android UI** — task entry, visible plan, approval prompts, run status, and history.
2. **Agent coordinator** — keeps each task in an explicit state machine: draft → planned → awaiting approval → running → verifying → completed/failed/cancelled.
3. **Model adapter** — sends a minimal task context to a configured model API and validates structured responses.
4. **Tool registry** — allowlisted, narrowly scoped operations. The model may propose a tool call; the app validates it before execution.
5. **Approval gate** — required for actions that send messages, change account settings, expose personal data, delete files, spend money, or otherwise have consequential effects.
6. **Memory store** — opt-in, inspectable, editable, and deletable records. Do not treat memory as model-weight training.
7. **Audit log** — local task summaries, tool names, approval results, and errors. Never log API keys or full private content by default.

## Trust boundary

Model output and web content are untrusted input. Neither may override app policy. Tool execution happens in app-owned code, not in arbitrary model-generated shell commands. Secrets must not be embedded in the APK or committed to the repository.

## Future Android integrations

Use Android intents and official app APIs when available. AccessibilityService is a last-resort, user-enabled integration for eligible UI automation; it must be transparent, narrowly scoped, and designed around Android platform policies. This starter does not implement AccessibilityService.

## First implementation sequence

1. Compile and test the empty app shell.
2. Add task data types and a deterministic planner.
3. Add persistence for user-approved task history.
4. Add a server-side model proxy and structured tool calling.
5. Add one harmless, allowlisted Android action and verification.
6. Add opt-in permissions and additional tools one at a time.
