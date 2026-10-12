# Roadmap

## M0 — Repository and build foundation
- [x] Create a dedicated repository.
- [x] Add native Android project structure and a minimal Compose screen.
- [x] Add security and architecture notes.
- [x] Add a basic CI workflow.
- [ ] Confirm CI passes on GitHub.
- [ ] Add Gradle wrapper and verify reproducible builds.

## M1 — Local task loop
- [ ] Task input and validation.
- [ ] Deterministic task-plan model.
- [ ] Visible task state machine: planned, awaiting approval, running, verifying, done, failed, cancelled.
- [ ] Cancel and timeout behavior.
- [ ] Unit tests for planner and approval rules.

## M2 — Memory
- [ ] Local Room database for user-approved notes and task summaries.
- [ ] Search, edit, export, and delete memory.
- [ ] Retention controls and sensitive-data exclusions.

## M3 — AI connection
- [ ] Backend proxy using a server-held provider key.
- [ ] Structured outputs and tool-call validation.
- [ ] Rate limits, cost caps, request timeouts, and redacted observability.
- [ ] Clear status when the model or network is unavailable.

## M4 — Narrow Android actions
- [ ] Start with a harmless, allowlisted intent.
- [ ] Require confirmation for consequential actions.
- [ ] Verify results and report partial failures.
- [ ] Evaluate any AccessibilityService use separately and transparently.

## M5 — Improvement loop
- [ ] Save user-approved lessons from completed tasks.
- [ ] Add regression tests for previously fixed failures.
- [ ] Propose code changes as reviewable pull requests, never silently self-deploy.
