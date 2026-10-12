# Roadmap

## M0 — Repository and build foundation
- [x] Create dedicated repository.
- [x] Add native Android project structure.
- [x] Add security and architecture notes.
- [x] Add GitHub Actions build workflow.
- [x] Confirm Android debug build and APK artifact.

## M1 — Local task-planning loop
- [x] Bangla task input and basic validation.
- [x] Deterministic task-plan model.
- [x] Flag potentially consequential tasks for confirmation.
- [x] Add planner unit tests and fix a missed Bangla message pattern from CI feedback.
- [x] Show a visible plan in the Android UI.

## M2 — Local memory
- [x] Add app-private SQLite storage for plans the user explicitly chooses to save.
- [x] Show saved plans in the app.
- [x] Allow individual saved plans to be deleted.
- [ ] Add SQLite instrumentation tests for persistence and deletion.

## M3 — AI backend prototype
- [x] Add an environment-configured Node.js API wrapper around the OpenAI Responses API.
- [x] Keep API credentials and the app bearer token outside the repository.
- [x] Add bearer-token authentication, request-size limits, task-length validation, and simple rate limiting.
- [x] Validate structured model output and re-check sensitive actions server-side.
- [x] Add credential-free backend tests and CI.
- [ ] Deploy the backend with HTTPS and secret environment variables.
- [ ] Connect Android to a user-configurable server URL and token.
- [ ] Add per-user usage/cost limits and shared rate limiting before multi-user deployment.

## M4 — Narrow Android actions
- [ ] Start with a harmless, allowlisted intent.
- [ ] Require explicit confirmation for consequential actions.
- [ ] Verify results and report partial failures.
- [ ] Evaluate any AccessibilityService use separately and transparently.

## M5 — Improvement loop
- [ ] Save user-approved lessons from completed tasks.
- [ ] Add regression tests for fixed failures.
- [ ] Propose code changes as reviewable commits or pull requests, never silently self-deploy.
