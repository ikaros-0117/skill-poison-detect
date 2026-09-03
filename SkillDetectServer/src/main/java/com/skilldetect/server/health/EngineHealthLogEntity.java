package com.skilldetect.server.health;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("engine_health_log")
public class EngineHealthLogEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("engine_version")
    private String engineVersion;

    @TableField("provider")
    private String provider;

    @TableField("llm_available")
    private Boolean llmAvailable;

    @TableField("active_scans")
    private Integer activeScans;

    @TableField("queue_depth")
    private Integer queueDepth;

    @TableField("latency_ms")
    private Integer latencyMs;

    @TableField("status")
    private String status;

    @TableField("checked_at")
    private LocalDateTime checkedAt = LocalDateTime.now();

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
    public LocalDateTime getCheckedAt() { return checkedAt; }
    public void setCheckedAt(LocalDateTime checkedAt) { this.checkedAt = checkedAt; }
}
