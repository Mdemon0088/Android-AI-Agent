import test from "node:test";
import assert from "node:assert/strict";
import { createHttpServer } from "../src/app.js";

const validPlan = {
  summary: "একটি প্রাথমিক পরিকল্পনা।",
  steps: [
    { title: "লক্ষ্য বুঝুন", detail: "কাজটির প্রত্যাশিত ফলাফল স্পষ্ট করুন।" },
    { title: "ধাপ সাজান", detail: "ছোট ছোট বাস্তবায়নযোগ্য ধাপ নির্ধারণ করুন।" },
    { title: "ফল যাচাই করুন", detail: "ফলাফল নির্দিষ্ট মানদণ্ডে পরীক্ষা করুন।" }
  ],
  requiresApproval: false,
  approvalReason: ""
};

function fakeFetchFor(plan = validPlan, status = 200) {
  return async (_url, _options) => ({
    ok: status >= 200 && status < 300,
    status,
    json: async () => ({
      output: [{
        type: "message",
        content: [{ type: "output_text", text: JSON.stringify(plan) }]
      }]
    })
  });
}

async function withServer(options, callback) {
  const server = createHttpServer(options);
  await new Promise((resolve) => server.listen(0, "127.0.0.1", resolve));
  const address = server.address();
  const baseUrl = `http://127.0.0.1:${address.port}`;
  try {
    return await callback(baseUrl);
  } finally {
    await new Promise((resolve) => {
      server.close(() => resolve());
      server.closeAllConnections?.();
    });
  }
}

function postPlan(baseUrl, body, token = "test-agent-token") {
  return fetch(`${baseUrl}/v1/plan`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {})
    },
    body
  });
}

const configured = {
  apiKey: "test-provider-key",
  model: "test-model",
  agentToken: "test-agent-token",
  fetchImpl: fakeFetchFor()
};

test("health endpoint reports readiness without exposing credentials", async () => {
  await withServer(configured, async (baseUrl) => {
    const response = await fetch(`${baseUrl}/health`);
    assert.equal(response.status, 200);
    assert.deepEqual(await response.json(), { ok: true, service: "nur-ai-api", configured: true });
  });
});

test("plan endpoint rejects missing authentication", async () => {
  let called = false;
  await withServer({ ...configured, fetchImpl: async () => { called = true; } }, async (baseUrl) => {
    const response = await postPlan(baseUrl, JSON.stringify({ task: "Plan my study week" }), "");
    assert.equal(response.status, 401);
    assert.equal(called, false);
  });
});

test("plan endpoint returns a normalized plan without executing actions", async () => {
  await withServer(configured, async (baseUrl) => {
    const response = await postPlan(baseUrl, JSON.stringify({ task: "আমার পড়াশোনার রুটিন তৈরি করো" }));
    const body = await response.json();
    assert.equal(response.status, 200);
    assert.equal(body.ok, true);
    assert.equal(body.executed, false);
    assert.equal(body.plan.requiresApproval, false);
    assert.equal(body.plan.steps.length, 3);
  });
});

test("server-side safety check overrides an unsafe model approval decision", async () => {
  const unsafeModelPlan = { ...validPlan, requiresApproval: false, approvalReason: "" };
  await withServer({ ...configured, fetchImpl: fakeFetchFor(unsafeModelPlan) }, async (baseUrl) => {
    const response = await postPlan(baseUrl, JSON.stringify({ task: "delete my old messages" }));
    const body = await response.json();
    assert.equal(response.status, 200);
    assert.equal(body.plan.requiresApproval, true);
    assert.match(body.plan.approvalReason, /অনুমতি/);
    assert.equal(body.executed, false);
    assert.ok(body.plan.steps.some((step) => /অনুমতি/.test(step.title)));
  });
});

test("empty tasks are rejected before a provider call", async () => {
  let called = false;
  await withServer({ ...configured, fetchImpl: async () => { called = true; } }, async (baseUrl) => {
    const response = await postPlan(baseUrl, JSON.stringify({ task: "   " }));
    assert.equal(response.status, 400);
    assert.equal(called, false);
  });
});

test("rate limiter rejects requests beyond the configured window", async () => {
  await withServer({ ...configured, rateLimitMax: 1 }, async (baseUrl) => {
    const first = await postPlan(baseUrl, JSON.stringify({ task: "Plan a study timetable" }));
    const second = await postPlan(baseUrl, JSON.stringify({ task: "Plan a study timetable" }));
    assert.equal(first.status, 200);
    assert.equal(second.status, 429);
  });
});

test("provider errors are hidden behind a generic response", async () => {
  await withServer({ ...configured, fetchImpl: fakeFetchFor(validPlan, 429) }, async (baseUrl) => {
    const response = await postPlan(baseUrl, JSON.stringify({ task: "Plan a study timetable" }));
    assert.equal(response.status, 502);
    assert.deepEqual(await response.json(), { error: "The AI provider request failed." });
  });
});

test("malformed JSON is rejected", async () => {
  await withServer(configured, async (baseUrl) => {
    const response = await postPlan(baseUrl, "{invalid json");
    assert.equal(response.status, 400);
  });
});
