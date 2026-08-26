"""SkillSpector engine thin wrapper.

Exposes the SkillSpector scan core as a plain HTTP service so the Spring Boot
SkillDetectServer can call it without touching Python internals.

Internal-only service: the Spring Boot server passes shared-volume local paths
and is trusted (``allow_local_targets=True``). A shared token can be added later
via an optional ``X-Engine-Token`` check.
"""

from __future__ import annotations

import asyncio
import os
import tempfile
import time
from contextlib import asynccontextmanager
from pathlib import Path
from typing import Any
from uuid import uuid4

from fastapi import FastAPI, HTTPException, Query
from pydantic import BaseModel, Field

from skillspector import __version__
from skillspector.cleanup import cleanup_result
from skillspector.constants import RISK_THRESHOLD
from skillspector.graph import graph
from skillspector.llm_utils import is_llm_available
from skillspector.suppression import effective_findings, load_baseline

ENGINE_MAX_CONCURRENT_SCANS = int(os.getenv("ENGINE_MAX_CONCURRENT_SCANS", "8"))
VALID_FORMATS = ("json", "sarif", "markdown", "terminal")

_semaphore: asyncio.Semaphore
_started_at: float
_active_tasks: dict[str, asyncio.Task[Any]] = {}
_stats: dict[str, Any] = {
    "succeeded": 0,
    "failed": 0,
    "last_success_ts": None,
    "last_failure_ts": None,
}


@asynccontextmanager
async def lifespan(_: FastAPI):
    global _semaphore, _started_at
    _semaphore = asyncio.Semaphore(ENGINE_MAX_CONCURRENT_SCANS)
    _started_at = time.time()
    try:
        yield
    finally:
        for task in list(_active_tasks.values()):
            task.cancel()


app = FastAPI(title="skillspector-engine", version=__version__, lifespan=lifespan)


class ScanRequest(BaseModel):
    path: str = Field(..., description="Shared-volume local path to the skill zip/dir.")
    use_llm: bool = Field(False, description="Request the optional LLM semantic pass.")
    output_format: str = Field("json", description="json | sarif | markdown | terminal")
    baseline: str | None = Field(None, description="Optional baseline YAML/JSON content (false-positive suppression).")


async def _run_scan(
    path: str,
    use_llm: bool,
    output_format: str,
    baseline_content: str | None,
) -> dict[str, Any]:
    """Run the SkillSpector graph, optionally applying a baseline, and return a verdict."""
    llm_available, _ = is_llm_available()
    llm_used = use_llm and llm_available

    state: dict[str, Any] = {
        "input_path": path,
        "output_format": output_format,
        "use_llm": llm_used,
    }

    baseline_file = None
    if baseline_content:
        baseline_file = tempfile.NamedTemporaryFile(mode="w", suffix=".yaml", delete=False)
        baseline_file.write(baseline_content)
        baseline_file.close()
        state["baseline"] = load_baseline(baseline_file.name)
        state["baseline_path"] = os.path.abspath(baseline_file.name)

    result: dict[str, Any] | None = None
    try:
        result = await graph.ainvoke(
            state,
            config={
                "run_name": "skillspector-engine-scan",
                "tags": ["skillspector", "engine"],
                "metadata": {
                    "input_path": path,
                    "use_llm": llm_used,
                    "output_format": output_format,
                    "version": __version__,
                },
            },
        )
        findings = effective_findings(result)
        risk_score = int(result.get("risk_score") or 0)
        execution_successful = bool(result.get("execution_successful", True))
        analysis_completeness = result.get("analysis_completeness") or {}
        entirely_uninspected = int(analysis_completeness.get("entirely_uninspected_files", 0))
        safe_to_install = (
            risk_score <= RISK_THRESHOLD
            and execution_successful
            and entirely_uninspected == 0
            and bool(analysis_completeness.get("is_complete", True))
        )
        return {
            "target": path,
            "risk_score": risk_score,
            "severity": result.get("risk_severity"),
            "recommendation": result.get("risk_recommendation"),
            "safe_to_install": safe_to_install,
            "execution_successful": execution_successful,
            "analysis_completeness": analysis_completeness,
            "findings": [f.to_dict() for f in findings],
            "report": result.get("report_body") or "",
            "llm_requested": use_llm,
            "llm_available": llm_available,
            "llm_used": llm_used,
            "scan_mode": "static+llm" if llm_used else "static-only",
            "version": __version__,
        }
    finally:
        if result is not None:
            cleanup_result(result)
        if baseline_file is not None:
            try:
                os.unlink(baseline_file.name)
            except OSError:
                pass


@app.get("/health")
async def health() -> dict[str, Any]:
    """Liveness + capability probe (cheap; no real scan)."""
    llm_ok, llm_err = is_llm_available()
    return {
        "status": "UP",
        "version": __version__,
        "provider": os.getenv("SKILLSPECTOR_PROVIDER", "unset"),
        "model": os.getenv("SKILLSPECTOR_MODEL") or None,
        "llm_available": bool(llm_ok),
        "llm_error": llm_err,
        "max_concurrent_scans": ENGINE_MAX_CONCURRENT_SCANS,
        "active_scans": len(_active_tasks),
        "stats": _stats,
        "uptime_seconds": round(time.time() - _started_at, 2),
    }


@app.get("/health/deep")
async def health_deep(timeout: float = Query(30.0, gt=0, le=120)) -> dict[str, Any]:
    """End-to-end probe: scan a tiny fixture with static analysis only."""
    fixture = Path(__file__).resolve().parent / "fixtures" / "healthy_skill"
    started = time.time()
    try:
        result = await asyncio.wait_for(
            _run_scan(str(fixture), use_llm=False, output_format="json", baseline_content=None),
            timeout=timeout,
        )
        ok = isinstance(result, dict) and "risk_score" in result and "findings" in result
        return {
            "status": "UP" if ok else "DOWN",
            "probe": "static-scan",
            "risk_score": result.get("risk_score"),
            "findings_count": len(result.get("findings", [])),
            "elapsed_ms": round((time.time() - started) * 1000),
        }
    except Exception as exc:  # noqa: BLE001 - health probe must report any failure
        return {"status": "DOWN", "probe": "static-scan", "error": str(exc)}


@app.post("/v1/scan")
async def scan(req: ScanRequest) -> dict[str, Any]:
    """Run one synchronous scan. Bounded by ENGINE_MAX_CONCURRENT_SCANS."""
    if req.output_format not in VALID_FORMATS:
        raise HTTPException(status_code=400, detail=f"output_format must be one of {VALID_FORMATS}")

    path = Path(req.path).expanduser()
    if not path.exists():
        raise HTTPException(status_code=400, detail=f"scan path not found: {req.path}")

    scan_id = uuid4().hex
    started = time.time()
    async with _semaphore:
        current = asyncio.current_task()
        if current is not None:
            _active_tasks[scan_id] = current
        try:
            result = await _run_scan(str(path), req.use_llm, req.output_format, req.baseline)
            _stats["succeeded"] += 1
            _stats["last_success_ts"] = time.time()
            return {
                **result,
                "engine_scan_id": scan_id,
                "engine_elapsed_ms": round((time.time() - started) * 1000),
            }
        except asyncio.CancelledError:
            _stats["failed"] += 1
            raise
        except Exception as exc:  # noqa: BLE001 - return an honest failure payload
            _stats["failed"] += 1
            _stats["last_failure_ts"] = time.time()
            raise HTTPException(status_code=500, detail=f"scan failed: {exc}") from exc
        finally:
            _active_tasks.pop(scan_id, None)


@app.post("/v1/scan/cancel")
async def cancel(scan_id: str) -> dict[str, Any]:
    """Best-effort cancel of an in-flight scan. LLM calls may not stop instantly."""
    task = _active_tasks.get(scan_id)
    if task is None:
        raise HTTPException(status_code=404, detail=f"scan not found or not running: {scan_id}")
    task.cancel()
    return {"scan_id": scan_id, "status": "cancelling"}
