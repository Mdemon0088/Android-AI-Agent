import { createHttpServer } from "./app.js";

const port = Number.parseInt(process.env.PORT ?? "8787", 10);
const server = createHttpServer({
  apiKey: process.env.OPENAI_API_KEY ?? "",
  model: process.env.OPENAI_MODEL ?? "",
  agentToken: process.env.NUR_AGENT_TOKEN ?? ""
});

server.listen(port, "0.0.0.0", () => {
  console.log(`NŪR AI API listening on port ${port}`);
});
