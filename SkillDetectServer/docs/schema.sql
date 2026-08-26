-- SkillDetectServer PostgreSQL schema (review draft)
-- PostgreSQL 16+. Redis is NOT the system of record; these tables are authoritative.

-- ---------------------------------------------------------------
-- 1. Scan task (authoritative record; Redis stream only mirrors it)
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS scan_task (
    id                          BIGSERIAL PRIMARY KEY,
    task_no                     VARCHAR(64)  NOT NULL UNIQUE,          -- e.g. t_<ulid/uuid>
    source_type                 VARCHAR(16)  NOT NULL DEFAULT 'upload', -- upload | url | git
    source_path                 TEXT,                                  -- /data/<taskId>/skill (shared volume)
    zip_sha256                  VARCHAR(64),
    zip_size_bytes              BIGINT,
    use_llm                     BOOLEAN      NOT NULL DEFAULT FALSE,
    risk_threshold              INTEGER      NOT NULL DEFAULT 50,      -- configurable gate threshold
    baseline_id                 BIGINT,                                -- optional FK scan_baseline
    status                      VARCHAR(16)  NOT NULL DEFAULT 'PENDING',
                                  -- PENDING | QUEUED | RUNNING | SUCCEEDED | FAILED | CANCELED
    risk_score                  INTEGER,                               -- 0..100
    severity                    VARCHAR(16),                           -- LOW | MEDIUM | HIGH | CRITICAL
    recommendation              VARCHAR(32),                           -- SAFE | CAUTION | REVIEW | ...
    safe_to_install             BOOLEAN,                               -- engine verdict (engine threshold)
    pass                        BOOLEAN,                               -- final gate = risk_score <= risk_threshold
    execution_successful        BOOLEAN,
    analysis_complete           BOOLEAN,
    entirely_uninspected_files  INTEGER,
    llm_used                    BOOLEAN,
    scan_mode                   VARCHAR(16),                           -- static-only | static+llm
    engine_scan_id              VARCHAR(64),                           -- engine-side scan id (for cancel)
    report_format               VARCHAR(16),                           -- json | markdown | sarif
    report_path                 TEXT,                                  -- shared volume report file path
    sarif_path                  TEXT,
    metadata                    JSONB,                                 -- repo/commit/pipelineId/...
    error_code                  VARCHAR(64),
    error_msg                   TEXT,
    retry_count                 INTEGER      NOT NULL DEFAULT 0,
    created_by                  VARCHAR(128),                          -- reserved for SSO principal
    created_at                  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    started_at                  TIMESTAMPTZ,
    finished_at                 TIMESTAMPTZ,
    updated_at                  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_scan_task_status          ON scan_task (status);
CREATE INDEX IF NOT EXISTS idx_scan_task_created_at      ON scan_task (created_at DESC);
CREATE INDEX IF NOT EXISTS idx_scan_task_finished_at     ON scan_task (finished_at); -- 30d cleanup
CREATE INDEX IF NOT EXISTS idx_scan_task_created_by      ON scan_task (created_by);
-- Reconciler / dispatcher recovery uses this partial index
CREATE INDEX IF NOT EXISTS idx_scan_task_unfinished
    ON scan_task (status, created_at)
    WHERE status IN ('PENDING', 'QUEUED', 'RUNNING');

-- ---------------------------------------------------------------
-- 2. Findings (one row per engine finding)
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS scan_finding (
    id              BIGSERIAL PRIMARY KEY,
    task_id         BIGINT        NOT NULL REFERENCES scan_task(id) ON DELETE CASCADE,
    finding_id      VARCHAR(64)   NOT NULL,               -- engine finding id
    rule_id         VARCHAR(32)   NOT NULL,               -- e.g. PI1, AST1, YR1
    severity        VARCHAR(16)   NOT NULL,               -- LOW | MEDIUM | HIGH | CRITICAL
    category        VARCHAR(64),
    pattern         VARCHAR(64),
    file            VARCHAR(1024),
    start_line      INTEGER,
    end_line        INTEGER,
    message         TEXT,
    explanation     TEXT,
    remediation     TEXT,
    confidence      DOUBLE PRECISION,
    matched_text    TEXT,
    fingerprint     VARCHAR(128),
    source_url      TEXT,
    transitive_depth INTEGER,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_scan_finding_task       ON scan_finding (task_id);
CREATE INDEX IF NOT EXISTS idx_scan_finding_task_sev   ON scan_finding (task_id, severity);
CREATE INDEX IF NOT EXISTS idx_scan_finding_rule       ON scan_finding (rule_id);
CREATE INDEX IF NOT EXISTS idx_scan_finding_category   ON scan_finding (category);

-- ---------------------------------------------------------------
-- 3. Baseline (false-positive suppression) — reserved, P2
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS scan_baseline (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(128) NOT NULL,
    content     TEXT         NOT NULL,   -- YAML or JSON baseline body
    format      VARCHAR(8)   NOT NULL DEFAULT 'yaml', -- yaml | json
    version     VARCHAR(32),
    created_by  VARCHAR(128),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- ---------------------------------------------------------------
-- 4. API credentials — reserved for M2M key auth (disabled initially)
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS api_credential (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(128) NOT NULL,
    key_hash    VARCHAR(128) NOT NULL UNIQUE,   -- only store hash, never plaintext
    scopes      TEXT[]       NOT NULL DEFAULT '{}',
    enabled     BOOLEAN      NOT NULL DEFAULT TRUE,
    expires_at  TIMESTAMPTZ,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- ---------------------------------------------------------------
-- 5. Engine health log (optional, observability)
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS engine_health_log (
    id              BIGSERIAL PRIMARY KEY,
    engine_version  VARCHAR(32),
    provider        VARCHAR(32),
    llm_available   BOOLEAN,
    active_scans    INTEGER,
    queue_depth     INTEGER,
    latency_ms      INTEGER,
    status          VARCHAR(16),   -- UP | DOWN | DEGRADED
    checked_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_engine_health_checked_at ON engine_health_log (checked_at DESC);
