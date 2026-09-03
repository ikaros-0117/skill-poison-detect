package com.skilldetect.server.scan.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/** Response DTOs shared by HMI and M2M controllers. */
public final class ScanDtos {

    private ScanDtos() {}

    public static final class Create {
        private final String taskId;
        public Create(String taskId) { this.taskId = taskId; }
        public String getTaskId() { return taskId; }
    }

    public static final class FindingsSummary {
        private final int critical;
        private final int high;
        private final int medium;
        private final int low;
        public FindingsSummary(int critical, int high, int medium, int low) {
            this.critical = critical;
            this.high = high;
            this.medium = medium;
            this.low = low;
        }
        public int getCritical() { return critical; }
        public int getHigh() { return high; }
        public int getMedium() { return medium; }
        public int getLow() { return low; }
    }

    public static final class Task {
        private final String taskId;
        private final String status;
        private final Integer riskScore;
        private final String severity;
        private final String recommendation;
        private final Boolean safeToInstall;
        private final Boolean pass;
        private final Boolean executionSuccessful;
        private final Map<String, Object> analysisCompleteness;
        private final Boolean llmUsed;
        private final String scanMode;
        private final FindingsSummary findingsSummary;
        private final String reportUrl;
        private final String sarifUrl;
        private final String errorCode;
        private final String errorMsg;
        private final Map<String, Object> metadata;
        private final LocalDateTime createdAt;
        private final LocalDateTime startedAt;
        private final LocalDateTime finishedAt;

        public Task(String taskId,
                    String status,
                    Integer riskScore,
                    String severity,
                    String recommendation,
                    Boolean safeToInstall,
                    Boolean pass,
                    Boolean executionSuccessful,
                    Map<String, Object> analysisCompleteness,
                    Boolean llmUsed,
                    String scanMode,
                    FindingsSummary findingsSummary,
                    String reportUrl,
                    String sarifUrl,
                    String errorCode,
                    String errorMsg,
                    Map<String, Object> metadata,
                    LocalDateTime createdAt,
                    LocalDateTime startedAt,
                    LocalDateTime finishedAt) {
            this.taskId = taskId;
            this.status = status;
            this.riskScore = riskScore;
            this.severity = severity;
            this.recommendation = recommendation;
            this.safeToInstall = safeToInstall;
            this.pass = pass;
            this.executionSuccessful = executionSuccessful;
            this.analysisCompleteness = analysisCompleteness;
            this.llmUsed = llmUsed;
            this.scanMode = scanMode;
            this.findingsSummary = findingsSummary;
            this.reportUrl = reportUrl;
            this.sarifUrl = sarifUrl;
            this.errorCode = errorCode;
            this.errorMsg = errorMsg;
            this.metadata = metadata;
            this.createdAt = createdAt;
            this.startedAt = startedAt;
            this.finishedAt = finishedAt;
        }

        public String getTaskId() { return taskId; }
        public String getStatus() { return status; }
        public Integer getRiskScore() { return riskScore; }
        public String getSeverity() { return severity; }
        public String getRecommendation() { return recommendation; }
        public Boolean getSafeToInstall() { return safeToInstall; }
        public Boolean getPass() { return pass; }
        public Boolean getExecutionSuccessful() { return executionSuccessful; }
        public Map<String, Object> getAnalysisCompleteness() { return analysisCompleteness; }
        public Boolean getLlmUsed() { return llmUsed; }
        public String getScanMode() { return scanMode; }
        public FindingsSummary getFindingsSummary() { return findingsSummary; }
        public String getReportUrl() { return reportUrl; }
        public String getSarifUrl() { return sarifUrl; }
        public String getErrorCode() { return errorCode; }
        public String getErrorMsg() { return errorMsg; }
        public Map<String, Object> getMetadata() { return metadata; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public LocalDateTime getStartedAt() { return startedAt; }
        public LocalDateTime getFinishedAt() { return finishedAt; }
    }

    public static final class Finding {
        private final String findingId;
        private final String ruleId;
        private final String severity;
        private final String category;
        private final String pattern;
        private final String file;
        private final Integer startLine;
        private final Integer endLine;
        private final String message;
        private final String explanation;
        private final String remediation;
        private final Double confidence;
        private final String matchedText;
        private final String sourceUrl;
        private final Integer transitiveDepth;

        public Finding(String findingId,
                       String ruleId,
                       String severity,
                       String category,
                       String pattern,
                       String file,
                       Integer startLine,
                       Integer endLine,
                       String message,
                       String explanation,
                       String remediation,
                       Double confidence,
                       String matchedText,
                       String sourceUrl,
                       Integer transitiveDepth) {
            this.findingId = findingId;
            this.ruleId = ruleId;
            this.severity = severity;
            this.category = category;
            this.pattern = pattern;
            this.file = file;
            this.startLine = startLine;
            this.endLine = endLine;
            this.message = message;
            this.explanation = explanation;
            this.remediation = remediation;
            this.confidence = confidence;
            this.matchedText = matchedText;
            this.sourceUrl = sourceUrl;
            this.transitiveDepth = transitiveDepth;
        }

        public String getFindingId() { return findingId; }
        public String getRuleId() { return ruleId; }
        public String getSeverity() { return severity; }
        public String getCategory() { return category; }
        public String getPattern() { return pattern; }
        public String getFile() { return file; }
        public Integer getStartLine() { return startLine; }
        public Integer getEndLine() { return endLine; }
        public String getMessage() { return message; }
        public String getExplanation() { return explanation; }
        public String getRemediation() { return remediation; }
        public Double getConfidence() { return confidence; }
        public String getMatchedText() { return matchedText; }
        public String getSourceUrl() { return sourceUrl; }
        public Integer getTransitiveDepth() { return transitiveDepth; }
    }

    public static final class Page<T> {
        private final List<T> items;
        private final int page;
        private final int size;
        private final long total;

        public Page(List<T> items, int page, int size, long total) {
            this.items = items;
            this.page = page;
            this.size = size;
            this.total = total;
        }

        public List<T> getItems() { return items; }
        public int getPage() { return page; }
        public int getSize() { return size; }
        public long getTotal() { return total; }
    }
}
