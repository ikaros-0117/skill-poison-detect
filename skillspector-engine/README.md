# skillspector-engine

Thin FastAPI wrapper around the SkillSpector scan core
(`skillspector.mcp_server.run_scan`). This is an **internal-only** service that
the Spring Boot `SkillDetectServer` calls over HTTP. It is not exposed publicly.

## Endpoints

| Method | Path | Description |
|---|---|---|
| GET | `/health` | Liveness/capability probe: version, provider, LLM availability, active scans |
| GET | `/health/deep?timeout=30` | End-to-end probe: scans `fixtures/healthy_skill` (static only) |
| POST | `/v1/scan` | Run one synchronous scan; body `{path, use_llm, output_format}` |
| POST | `/v1/scan/cancel?scan_id=` | Best-effort cancel of an in-flight scan |

## Configuration (environment)

| Variable | Default | Notes |
|---|---|---|
| `ENGINE_MAX_CONCURRENT_SCANS` | `8` | Per-process concurrency limit for scans |
| `SKILLSPECTOR_PROVIDER` | unset | Set to `openai` for the internal OpenAI-compatible gateway |
| `OPENAI_BASE_URL` | unset | Internal gateway base URL (e.g. `https://gateway/v1`) |
| `OPENAI_API_KEY` | unset | Gateway API key |
| `SKILLSPECTOR_MODEL` | provider default | Optional model override |

## Local development

```bash
python3.12 -m venv .venv && source .venv/bin/activate
pip install ../SkillSpector
pip install -r requirements.txt
uvicorn app:app --reload --port 8000
```

## Docker

Build from the repository root so both `SkillSpector/` and `skillspector-engine/`
are in the build context:

```bash
docker build -f skillspector-engine/Dockerfile -t skillspector-engine .
docker run --rm -p 8000:8000 \
  -e SKILLSPECTOR_PROVIDER=openai \
  -e OPENAI_BASE_URL=https://internal-gateway/v1 \
  -e OPENAI_API_KEY=... \
  skillspector-engine
```
