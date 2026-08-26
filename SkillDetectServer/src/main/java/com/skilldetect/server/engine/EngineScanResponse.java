package com.skilldetect.server.engine;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EngineScanResponse {

    private String target;
    private Integer riskScore;
    private String severity;
    private String recommendation;
    private Boolean safeToInstall;
    private Boolean executionSuccessful;
    private Map<String, Object> analysisCompleteness;
    private List<Map<String, Object>> findings;
    private String report;
    private Boolean llmRequested;
    private Boolean llmAvailable;
    private Boolean llmUsed;
    private String scanMode;
    private String version;
    private String engineScanId;
    private Long engineElapsedMs;

    @JsonProperty("risk_score")
    public void setRiskScore(Integer riskScore) { this.riskScore = riskScore; }
    public Integer getRiskScore() { return riskScore; }

    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    @JsonProperty("safe_to_install")
    public void setSafeToInstall(Boolean safeToInstall) { this.safeToInstall = safeToInstall; }
    public Boolean getSafeToInstall() { return safeToInstall; }

    @JsonProperty("execution_successful")
    public void setExecutionSuccessful(Boolean executionSuccessful) { this.executionSuccessful = executionSuccessful; }
    public Boolean getExecutionSuccessful() { return executionSuccessful; }

    @JsonProperty("analysis_completeness")
    public void setAnalysisCompleteness(Map<String, Object> analysisCompleteness) { this.analysisCompleteness = analysisCompleteness; }
    public Map<String, Object> getAnalysisCompleteness() { return analysisCompleteness; }

    public List<Map<String, Object>> getFindings() { return findings; }
    public void setFindings(List<Map<String, Object>> findings) { this.findings = findings; }

    public String getReport() { return report; }
    public void setReport(String report) { this.report = report; }

    @JsonProperty("llm_requested")
    public void setLlmRequested(Boolean llmRequested) { this.llmRequested = llmRequested; }
    public Boolean getLlmRequested() { return llmRequested; }

    @JsonProperty("llm_available")
    public void setLlmAvailable(Boolean llmAvailable) { this.llmAvailable = llmAvailable; }
    public Boolean getLlmAvailable() { return llmAvailable; }

    @JsonProperty("llm_used")
    public void setLlmUsed(Boolean llmUsed) { this.llmUsed = llmUsed; }
    public Boolean getLlmUsed() { return llmUsed; }

    @JsonProperty("scan_mode")
    public void setScanMode(String scanMode) { this.scanMode = scanMode; }
    public String getScanMode() { return scanMode; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    @JsonProperty("engine_scan_id")
    public void setEngineScanId(String engineScanId) { this.engineScanId = engineScanId; }
    public String getEngineScanId() { return engineScanId; }

    @JsonProperty("engine_elapsed_ms")
    public void setEngineElapsedMs(Long engineElapsedMs) { this.engineElapsedMs = engineElapsedMs; }
    public Long getEngineElapsedMs() { return engineElapsedMs; }
}
