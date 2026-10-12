# NŪR AI API

A minimal Node.js 22+ backend for the NŪR AI Android prototype. It uses Node's built-in HTTP server and fetch, so no npm runtime dependencies are required.

## What this service does

- Exposes GET /health.
- Exposes authenticated POST /v1/plan.
- Calls the OpenAI Responses API from the server, using strict JSON Schema output.
- Enforces an 8 KiB request-body limit, a 1000-character task limit, bearer-token authentication, and a simple per-IP rate limit.
- Adds a deterministic server-side approval check for common sensitive actions, even if the model under-classifies them.
- Never executes tools or Android actions. Every response includes executed: false.
- Does not log task contents or forward the provider's raw error body to clients.

## Configure

Use Node.js 22 or later. Set these environment variables in the deployment environment:

- OPENAI_API_KEY — API credential stored only on the server.
- OPENAI_MODEL — a model available to the API project that supports Responses API structured outputs.
- NUR_AGENT_TOKEN — a long, random bearer token used by the personal app.
- PORT — optional, defaults to 8787.

The root README's .gitignore excludes environment files. Never commit a real API key or token.

## Test locally without paid API calls

```sh
npm test
```

The test suite injects a fake upstream fetch function. It does not call OpenAI and does not require credentials.

## Run

After configuring environment variables, run:

```sh
npm start
```

Then use GET /health to confirm the server is reachable. A health result with configured: false means one or more required environment variables are missing.

Example protected request:

```sh
curl -X POST http://localhost:8787/v1/plan \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer YOUR_NUR_AGENT_TOKEN' \
  -d '{"task":"আমার পড়াশোনার একটি সাপ্তাহিক রুটিন তৈরি করো"}'
```

## Important deployment notes

- Use HTTPS for any public deployment.
- Configure the token and provider key as host-side secrets, not GitHub files or Android app constants.
- The rate limiter is in-memory and intended for a single-user prototype; use a shared store and stronger identity/session controls before multi-user deployment.
- The Android app is not connected to this service yet. Do not deploy as a public open proxy.
