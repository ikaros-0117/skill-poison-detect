package com.skilldetect.server.scan.domain;

import java.time.Instant;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "scan_task")
public class ScanTaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_no", nullable = false, unique = true, length = 64)
    private String taskNo;

    @Column(name = "source_type", nullable = false, length = 16)
    private String sourceType = "upload";

    @Column(name = "source_path")
    private String sourcePath;

    @Column(name = "zip_sha256", length = 64)
    private String zipSha256;

    @Column(name = "zip_size_bytes")
    private Long zipSizeBytes;

    @Column(name = "baseline_id")
    private Long baselineId;

    @Column(name = "use_llm", nullable = false)
    private boolean useLlm;

    @Column(name = "risk_threshold", nullable = false)
    private Integer riskThreshold = 50;

    @Column(name = "status", nullable = false, length = 16)
    private String status = ScanStatus.QUEUED.name();

    @Column(name = "risk_score")
    private Integer riskScore;

    @Column(name = "severity", length = 16)
    private String severity;

    @Column(name = "recommendation", length = 32)
    private String recommendation;

    @Column(name = "safe_to_install")
    private Boolean safeToInstall;

    @Column(name = "pass")
    private Boolean pass;

    @Column(name = "execution_successful")
    private Boolean executionSuccessful;

    @Column(name = "analysis_complete")
    private Boolean analysisComplete;

    @Column(name = "entirely_uninspected_files")
    private Integer entirelyUninspectedFiles;

    @Column(name = "llm_used")
    private Boolean llmUsed;

    @Column(name = "scan_mode", length = 16)
    private String scanMode;

    @Column(name = "engine_scan_id", length = 64)
    private String engineScanId;

    @Column(name = "report_format", length = 16)
    private String reportFormat;

    @Column(name = "report_path")
    private String reportPath;

    @Column(name = "sarif_path")
    private String sarifPath;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Column(name = "error_code", length = 64)
    private String errorCode;

    @Column(name = "error_msg")
    private String errorMsg;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "created_by", length = 128)
    private String createdBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTaskNo() { return taskNo; }
    public void setTaskNo(String taskNo) { this.taskNo = taskNo; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public String getSourcePath() { return sourcePath; }
    public void setSourcePath(String sourcePath) { this.sourcePath = sourcePath; }
    public String getZipSha256() { return zipSha256; }
    public void setZipSha256(String zipSha256) { this.zipSha256 = zipSha256; }
    public Long getZipSizeBytes() { return zipSizeBytes; }
    public void setZipSizeBytes(Long zipSizeBytes) { this.zipSizeBytes = zipSizeBytes; }

    public Long getBaselineId() { return baselineId; }
    public void setBaselineId(Long baselineId) { this.baselineId = baselineId; }
    public boolean isUseLlm() { return useLlm; }
    public void setUseLlm(boolean useLlm) { this.useLlm = useLlm; }
    public Integer getRiskThreshold() { return riskThreshold; }
    public void setRiskThreshold(Integer riskThreshold) { this.riskThreshold = riskThreshold; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getRiskScore() { return riskScore; }
    public void setRiskScore(Integer riskScore) { this.riskScore = riskScore; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    public Boolean getSafeToInstall() { return safeToInstall; }
    public void setSafeToInstall(Boolean safeToInstall) { this.safeToInstall = safeToInstall; }
    public Boolean getPass() { return pass; }
    public void setPass(Boolean pass) { this.pass = pass; }
    public Boolean getExecutionSuccessful() { return executionSuccessful; }
    public void setExecutionSuccessful(Boolean executionSuccessful) { this.executionSuccessful = executionSuccessful; }
    public Boolean getAnalysisComplete() { return analysisComplete; }
    public void setAnalysisComplete(Boolean analysisComplete) { this.analysisComplete = analysisComplete; }
    public Integer getEntirelyUninspectedFiles() { return entirelyUninspectedFiles; }
    public void setEntirelyUninspectedFiles(Integer entirelyUninspectedFiles) { this.entirelyUninspectedFiles = entirelyUninspectedFiles; }
    public Boolean getLlmUsed() { return llmUsed; }
    public void setLlmUsed(Boolean llmUsed) { this.llmUsed = llmUsed; }
    public String getScanMode() { return scanMode; }
    public void setScanMode(String scanMode) { this.scanMode = scanMode; }
    public String getEngineScanId() { return engineScanId; }
    public void setEngineScanId(String engineScanId) { this.engineScanId = engineScanId; }
    public String getReportFormat() { return reportFormat; }
    public void setReportFormat(String reportFormat) { this.reportFormat = reportFormat; }
    public String getReportPath() { return reportPath; }
    public void setReportPath(String reportPath) { this.reportPath = reportPath; }
    public String getSarifPath() { return sarifPath; }
    public void setSarifPath(String sarifPath) { this.sarifPath = sarifPath; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    public String getErrorMsg() { return errorMsg; }
    public void setErrorMsg(String errorMsg) { this.errorMsg = errorMsg; }
    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getStartedAt() { return startedAt; }
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }
    public Instant getFinishedAt() { return finishedAt; }
    public void setFinishedAt(Instant finishedAt) { this.finishedAt = finishedAt; }
}
