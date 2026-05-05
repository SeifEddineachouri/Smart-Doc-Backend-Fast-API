# AiSmartDoc - Service-Local Setup Actions

## Why this file exists
This folder previously duplicated the full handoff content available in the workspace-level document.

Canonical handoff:
- `docs/AGENT_SETUP_ACTIONS.md`

Use this file only for actions specific to `AiSmartDoc` (AI gateway service).

---

## Service role in the 3-part architecture
- `smartdoc-ai` (Python/FastAPI): Gemini + ingestion + retrieval
- `AiSmartDoc` (Spring): AI gateway API that calls FastAPI
- Auth service (separate Spring): sign-in/sign-up/JWT/refresh-cookie

`AiSmartDoc` must not implement provider logic or duplicate auth domain logic.

---

## Mandatory actions for this folder
- Keep only AI gateway endpoints in `org.smartdoc.aismartdoc.ai`.
- Keep FastAPI integration in `FastApiClient` + `AiClientConfig` + `AiClientProperties`.
- Keep `server.port=8088` to avoid collision with other Spring services.
- Drive all sensitive values from env vars:
  - `APP_AI_BASE_URL`
  - `APP_AI_SERVICE_TOKEN`
  - `APP_AI_CONNECT_TIMEOUT_MS`
  - `APP_AI_READ_TIMEOUT_MS`

---

## De-duplication rule
Do not copy high-level setup plans into this folder again.
If a global plan changes, update only `docs/AGENT_SETUP_ACTIONS.md` and reference it from here.

---

## Quick validation for `AiSmartDoc`
- `GET /api/v1/ai/health` returns provider status when FastAPI is up.
- `POST /api/v1/ai/questions` forwards to FastAPI `/query`.
- `POST /api/v1/ai/ingest` forwards to FastAPI `/ingest`.
- Gateway returns `503` for provider outage (controlled failure).

