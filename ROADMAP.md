# Roadmap

## M0 — Repository and build foundation
- [x] Create dedicated repository.
- [x] Add native Android project structure and a minimal Compose screen.
- [x] Add security and architecture notes.
- [x] Add CI build workflow.
- [x] Confirm Android debug build passes on GitHub Actions.
- [ ] Add Gradle wrapper and verify reproducible local builds.

## M1 — Local task-planning loop
- [x] Bangla task input and basic validation.
- [x] Deterministic task-plan model.
- [x] Flag potentially consequential tasks for confirmation.
- [x] Add unit tests for planner and approval classification.
- [x] Show a visible plan in the Android UI.
- [ ] Expand classifier tests for ambiguous and adversarial inputs.
- [ ] Add UI/instrumentation tests on an emulator.

## M2 — Local memory
- [x] Add app-private SQLite storage for plans the user explicitly chooses to save.
- [x] Show saved plans in the app.
- [x] Allow individual saved plans to be deleted.
- [ ] Add SQLite instrumentation tests for save/list/delete and lifecycle behavior.
- [ ] Add a user-facing clear-all and export option after privacy review.
- [ ] Add memory retention controls and sensitive-data exclusions.

## M3 — AI connection
- [ ] Design a backend proxy using a server-held provider key.
- [ ] Add structured outputs and tool-call validation.
- [ ] Add rate limits, cost caps, request timeouts, and redacted observability.
- [ ] Show clear status when the model or network is unavailable.
- [ ] Keep model calls disabled until configuration and privacy notes are complete.

## M4 — Narrow Android actions
- [ ] Start with a harmless, allowlisted intent.
- [ ] Require confirmation for consequential actions.
- [ ] Verify results and report partial failures.
- [ ] Evaluate any AccessibilityService use separately and transparently.

## M5 — Improvement loop
- [ ] Save user-approved lessons from completed tasks.
- [ ] Add regression tests for previously fixed failures.
- [ ] Propose code changes as reviewable commits or pull requests, never silently self-deploy.
