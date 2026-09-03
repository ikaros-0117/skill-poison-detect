package com.skilldetect.server.scan.domain;

import java.time.LocalDateTime;
import java.util.Map;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;

@TableName(value = "scan_task", autoResultMap = true)
public class ScanTaskEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("task_no")
    private String taskNo;

    @TableField("source_type")
    private String sourceType = "upload";

    @TableField("source_path")
    private String sourcePath;

    @TableField("zip_sha256")
    private String zipSha256;

    @TableField("zip_size_bytes")
    private Long zipSizeBytes;

    @TableField("baseline_id")
    private Long baselineId;

    @TableField("use_llm")
    private boolean useLlm;

    @TableField("risk_threshold")
    private Integer riskThreshold = 50;

    @TableField("status")
    private String status = ScanStatus.QUEUED.name();

    @TableField("risk_score")
    private Integer riskScore;

    @TableField("severity")
    private String severity;

    @TableField("recommendation")
    private String recommendation;

    @TableField("safe_to_install")
    private Boolean safeToInstall;

    @TableField("pass")
    private Boolean pass;

    @TableField("execution_successful")
    private Boolean executionSuccessful;

    @TableField("analysis_complete")
    private Boolean analysisComplete;

    @TableField("entirely_uninspected_files")
    private Integer entirelyUninspectedFiles;

    @TableField("llm_used")
    private Boolean llmUsed;

    @TableField("scan_mode")
    private String scanMode;

    @TableField("engine_scan_id")
    private String engineScanId;

    @TableField("report_format")
    private String reportFormat;

    @TableField("report_path")
    private String reportPath;

    @TableField("sarif_path")
    private String sarifPath;

    @TableField(value = "metadata", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> metadata;

    @TableField("error_code")
    private String errorCode;

    @TableField("error_msg")
    private String errorMsg;

    @TableField("retry_count")
    private int retryCount;

    @TableField("created_by")
    private String createdBy;

    @TableField("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @TableField("started_at")
    private LocalDateTime startedAt;

    @TableField("finished_at")
    private LocalDateTime finishedAt;

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
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getFinishedAt() { return finishedAt; }
    public void setFinishedAt(LocalDateTime finishedAt) { this.finishedAt = finishedAt; }
}
