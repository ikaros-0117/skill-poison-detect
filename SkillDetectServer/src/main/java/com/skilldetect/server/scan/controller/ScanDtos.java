package com.skilldetect.server.scan.controller;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/** Response DTOs shared by HMI and M2M controllers. */
public final class ScanDtos {

    private ScanDtos() {}

    public record Create(String taskId) {}

    public record FindingsSummary(int critical, int high, int medium, int low) {}

    public record Task(
            String taskId,
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
            Instant createdAt,
            Instant startedAt,
            Instant finishedAt) {}

    public record Finding(
            String findingId,
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
            Integer transitiveDepth) {}

    public record Page<T>(List<T> items, int page, int size, long total) {}
}
