-- SkillDetectServer MySQL schema (8.0+)
-- Redis is NOT the system of record; these tables are authoritative.

CREATE DATABASE IF NOT EXISTS skilldetect
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE skilldetect;

-- ---------------------------------------------------------------
-- 1. Scan task (authoritative record; Redis list only mirrors it)
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS scan_task (
    id                          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    task_no                     VARCHAR(64)  NOT NULL UNIQUE,          -- e.g. t_<uuid>
    source_type                 VARCHAR(16)  NOT NULL DEFAULT 'upload', -- upload | url | git
    source_path                 TEXT,                                  -- /data/<taskId>/skill (shared volume)
    zip_sha256                  VARCHAR(64),
    zip_size_bytes              BIGINT,
    use_llm                     BOOLEAN      NOT NULL DEFAULT FALSE,
    risk_threshold              INT          NOT NULL DEFAULT 50,      -- configurable gate threshold
    baseline_id                 BIGINT,                                -- optional FK scan_baseline
    status                      VARCHAR(16)  NOT NULL DEFAULT 'PENDING',
                                  -- PENDING | QUEUED | RUNNING | SUCCEEDED | FAILED | CANCELED
    risk_score                  INT,                                   -- 0..100
    severity                    VARCHAR(16),                           -- LOW | MEDIUM | HIGH | CRITICAL
    recommendation              VARCHAR(32),                           -- SAFE | CAUTION | REVIEW | ...
    safe_to_install             BOOLEAN,                               -- engine verdict (engine threshold)
    pass                        BOOLEAN,                               -- final gate = risk_score <= risk_threshold
    execution_successful        BOOLEAN,
    analysis_complete           BOOLEAN,
    entirely_uninspected_files  INT,
    llm_used                    BOOLEAN,
    scan_mode                   VARCHAR(16),                           -- static-only | static+llm
    engine_scan_id              VARCHAR(64),                           -- engine-side scan id (for cancel)
    report_format               VARCHAR(16),                           -- json | markdown | sarif
    report_path                 TEXT,                                  -- shared volume report file path
    sarif_path                  TEXT,
    metadata                    JSON,                                  -- repo/commit/pipelineId/...
    error_code                  VARCHAR(64),
    error_msg                   TEXT,
    retry_count                 INT          NOT NULL DEFAULT 0,
    created_by                  VARCHAR(128),                          -- reserved for SSO principal
    created_at                  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    started_at                  DATETIME(3),
    finished_at                 DATETIME(3),
    updated_at                  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_scan_task_status          ON scan_task (status);
CREATE INDEX idx_scan_task_created_at      ON scan_task (created_at);
CREATE INDEX idx_scan_task_finished_at     ON scan_task (finished_at);
CREATE INDEX idx_scan_task_created_by      ON scan_task (created_by);
-- MySQL has no partial index; use a normal index for reconciler/dispatcher recovery scans.
CREATE INDEX idx_scan_task_unfinished      ON scan_task (status, created_at);

-- ---------------------------------------------------------------
-- 2. Findings (one row per engine finding)
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS scan_finding (
    id              BIGINT        NOT NULL AUTO_INCREMENT PRIMARY KEY,
    task_id         BIGINT        NOT NULL,
    finding_id      VARCHAR(64)   NOT NULL,               -- engine finding id
    rule_id         VARCHAR(32)   NOT NULL,               -- e.g. PI1, AST1, YR1
    severity        VARCHAR(16)   NOT NULL,               -- LOW | MEDIUM | HIGH | CRITICAL
    category        VARCHAR(64),
    pattern         VARCHAR(64),
    file            VARCHAR(1024),
    start_line      INT,
    end_line        INT,
    message         TEXT,
    explanation     TEXT,
    remediation     TEXT,
    confidence      DOUBLE,
    matched_text    TEXT,
    fingerprint     VARCHAR(128),
    source_url      TEXT,
    transitive_depth INT,
    created_at      DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    CONSTRAINT fk_scan_finding_task FOREIGN KEY (task_id) REFERENCES scan_task(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_scan_finding_task       ON scan_finding (task_id);
CREATE INDEX idx_scan_finding_task_sev   ON scan_finding (task_id, severity);
CREATE INDEX idx_scan_finding_rule       ON scan_finding (rule_id);
CREATE INDEX idx_scan_finding_category   ON scan_finding (category);

-- ---------------------------------------------------------------
-- 3. Baseline (false-positive suppression) — reserved, P2
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS scan_baseline (
    id          BIGINT        NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(128)  NOT NULL,
    content     TEXT          NOT NULL,   -- YAML or JSON baseline body
    format      VARCHAR(8)    NOT NULL DEFAULT 'yaml', -- yaml | json
    version     VARCHAR(32),
    created_by  VARCHAR(128),
    created_at  DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at  DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------
-- 4. API credentials — reserved for M2M key auth (disabled initially)
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS api_credential (
    id          BIGINT        NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(128)  NOT NULL,
    key_hash    VARCHAR(128)  NOT NULL UNIQUE,   -- only store hash, never plaintext
    scopes      JSON          NOT NULL,          -- JSON array of scopes
    enabled     BOOLEAN       NOT NULL DEFAULT TRUE,
    expires_at  DATETIME(3),
    created_at  DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at  DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------
-- 5. Engine health log (optional, observability)
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS engine_health_log (
    id              BIGINT        NOT NULL AUTO_INCREMENT PRIMARY KEY,
    engine_version  VARCHAR(32),
    provider        VARCHAR(32),
    llm_available   BOOLEAN,
    active_scans    INT,
    queue_depth     INT,
    latency_ms      INT,
    status          VARCHAR(16),   -- UP | DOWN | DEGRADED
    checked_at      DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_engine_health_checked_at ON engine_health_log (checked_at);
