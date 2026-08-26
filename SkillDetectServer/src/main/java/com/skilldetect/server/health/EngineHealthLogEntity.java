package com.skilldetect.server.health;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "engine_health_log")
public class EngineHealthLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "engine_version", length = 32)
    private String engineVersion;

    @Column(name = "provider", length = 32)
    private String provider;

    @Column(name = "llm_available")
    private Boolean llmAvailable;

    @Column(name = "active_scans")
    private Integer activeScans;

    @Column(name = "queue_depth")
    private Integer queueDepth;

    @Column(name = "latency_ms")
    private Integer latencyMs;

    @Column(name = "status", length = 16)
    private String status;

    @Column(name = "checked_at", nullable = false)
    private Instant checkedAt = Instant.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEngineVersion() { return engineVersion; }
    public void setEngineVersion(String engineVersion) { this.engineVersion = engineVersion; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public Boolean getLlmAvailable() { return llmAvailable; }
    public void setLlmAvailable(Boolean llmAvailable) { this.llmAvailable = llmAvailable; }
    public Integer getActiveScans() { return activeScans; }
    public void setActiveScans(Integer activeScans) { this.activeScans = activeScans; }
    public Integer getQueueDepth() { return queueDepth; }
    public void setQueueDepth(Integer queueDepth) { this.queueDepth = queueDepth; }
    public Integer getLatencyMs() { return latencyMs; }
    public void setLatencyMs(Integer latencyMs) { this.latencyMs = latencyMs; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCheckedAt() { return checkedAt; }
    public void setCheckedAt(Instant checkedAt) { this.checkedAt = checkedAt; }
}
