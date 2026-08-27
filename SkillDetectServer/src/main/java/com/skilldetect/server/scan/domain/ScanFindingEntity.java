package com.skilldetect.server.scan.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "scan_finding")
public class ScanFindingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_id", nullable = false)
    private Long taskId;

    @Column(name = "finding_id", nullable = false, length = 64)
    private String findingId;

    @Column(name = "rule_id", nullable = false, length = 32)
    private String ruleId;

    @Column(name = "severity", nullable = false, length = 16)
    private String severity;

    @Column(name = "category", length = 64)
    private String category;

    @Column(name = "pattern", length = 64)
    private String pattern;

    @Column(name = "file", length = 1024)
    private String file;

    @Column(name = "start_line")
    private Integer startLine;

    @Column(name = "end_line")
    private Integer endLine;

    @Column(name = "message", columnDefinition = "text")
    private String message;

    @Column(name = "explanation", columnDefinition = "text")
    private String explanation;

    @Column(name = "remediation", columnDefinition = "text")
    private String remediation;

    @Column(name = "confidence")
    private Double confidence;

    @Column(name = "matched_text", columnDefinition = "text")
    private String matchedText;

    @Column(name = "fingerprint", length = 128)
    private String fingerprint;

    @Column(name = "source_url", columnDefinition = "text")
    private String sourceUrl;

    @Column(name = "transitive_depth")
    private Integer transitiveDepth;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public String getFindingId() { return findingId; }
    public void setFindingId(String findingId) { this.findingId = findingId; }
    public String getRuleId() { return ruleId; }
    public void setRuleId(String ruleId) { this.ruleId = ruleId; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getPattern() { return pattern; }
    public void setPattern(String pattern) { this.pattern = pattern; }
    public String getFile() { return file; }
    public void setFile(String file) { this.file = file; }
    public Integer getStartLine() { return startLine; }
    public void setStartLine(Integer startLine) { this.startLine = startLine; }
    public Integer getEndLine() { return endLine; }
    public void setEndLine(Integer endLine) { this.endLine = endLine; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public String getRemediation() { return remediation; }
    public void setRemediation(String remediation) { this.remediation = remediation; }
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
    public String getMatchedText() { return matchedText; }
    public void setMatchedText(String matchedText) { this.matchedText = matchedText; }
    public String getFingerprint() { return fingerprint; }
    public void setFingerprint(String fingerprint) { this.fingerprint = fingerprint; }
    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }
    public Integer getTransitiveDepth() { return transitiveDepth; }
    public void setTransitiveDepth(Integer transitiveDepth) { this.transitiveDepth = transitiveDepth; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
