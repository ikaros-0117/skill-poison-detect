package com.skilldetect.server.scan.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skilldetect.server.common.BusinessException;
import com.skilldetect.server.config.ScanProperties;
import com.skilldetect.server.scan.controller.ScanDtos;
import com.skilldetect.server.scan.controller.ScanDtos.FindingsSummary;
import com.skilldetect.server.scan.controller.ScanDtos.Finding;
import com.skilldetect.server.scan.controller.ScanDtos.Task;
import com.skilldetect.server.scan.domain.ScanFindingEntity;
import com.skilldetect.server.scan.domain.ScanStatus;
import com.skilldetect.server.scan.domain.ScanTaskEntity;
import com.skilldetect.server.scan.queue.ScanQueueService;
import com.skilldetect.server.scan.repository.ScanFindingRepository;
import com.skilldetect.server.scan.repository.ScanTaskRepository;

import org.springframework.http.HttpStatus;

@Service
public class ScanService {

    private final ScanTaskRepository taskRepo;
    private final ScanFindingRepository findingRepo;
    private final ScanQueueService queueService;
    private final ScanStateService stateService;
    private final FileStorageService fileStorage;
    private final ScanProperties properties;

    public ScanService(ScanTaskRepository taskRepo,
                       ScanFindingRepository findingRepo,
                       ScanQueueService queueService,
                       ScanStateService stateService,
                       FileStorageService fileStorage,
                       ScanProperties properties) {
        this.taskRepo = taskRepo;
        this.findingRepo = findingRepo;
        this.queueService = queueService;
        this.stateService = stateService;
        this.fileStorage = fileStorage;
        this.properties = properties;
    }

    public String create(byte[] zipBytes, boolean useLlm, Integer riskThreshold,
                         String reportFormat, Long baselineId, Map<String, Object> metadata) {
        String taskNo = "t_" + UUID.randomUUID().toString().replace("-", "");
        String path = fileStorage.saveUpload(taskNo, zipBytes);

        ScanTaskEntity task = new ScanTaskEntity();
        task.setTaskNo(taskNo);
        task.setSourceType("upload");
        task.setSourcePath(path);
        task.setZipSizeBytes((long) zipBytes.length);
        task.setZipSha256(FileStorageService.sha256(zipBytes));
        task.setUseLlm(useLlm);
        task.setRiskThreshold(riskThreshold != null ? riskThreshold : properties.getRiskThreshold());
        task.setReportFormat(reportFormat);
        task.setBaselineId(baselineId);
        task.setMetadata(metadata);
        task.setStatus(ScanStatus.QUEUED.name());
        task.setCreatedAt(Instant.now());

        // Persist first (commits before returning), then enqueue to avoid the
        // dispatch race where a worker pops a taskNo before the row is visible.
        stateService.saveNewTask(task);
        queueService.enqueue(taskNo);
        return taskNo;
    }

    public ScanTaskEntity requireTask(String taskNo) {
        return taskRepo.findByTaskNo(taskNo)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, 40401, "任务不存在: " + taskNo));
    }

    public ScanDtos.Task toDto(ScanTaskEntity task) {
        List<ScanFindingEntity> findings = findingRepo.findByTaskId(task.getId());
        Map<String, Integer> summary = new LinkedHashMap<>();
        summary.put("CRITICAL", 0);
        summary.put("HIGH", 0);
        summary.put("MEDIUM", 0);
        summary.put("LOW", 0);
        for (ScanFindingEntity f : findings) {
            String sev = f.getSeverity() == null ? "LOW" : f.getSeverity().toUpperCase();
            summary.merge(sev, 1, Integer::sum);
        }
        FindingsSummary findingsSummary = new FindingsSummary(
                summary.getOrDefault("CRITICAL", 0),
                summary.getOrDefault("HIGH", 0),
                summary.getOrDefault("MEDIUM", 0),
                summary.getOrDefault("LOW", 0));

        Map<String, Object> completeness = new LinkedHashMap<>();
        completeness.put("isComplete", task.getAnalysisComplete());
        completeness.put("entirelyUninspectedFiles", task.getEntirelyUninspectedFiles());
        completeness.put("status", task.getStatus());

        String reportUrl = null;
        String sarifUrl = null;
        if (ScanStatus.SUCCEEDED.name().equals(task.getStatus())) {
            if ("sarif".equalsIgnoreCase(task.getReportFormat())) {
                sarifUrl = "/api/m2m/v1/scans/" + task.getTaskNo() + "/report/sarif";
            } else {
                reportUrl = "/api/v1/scans/" + task.getTaskNo() + "/report?format=json";
            }
        }

        return new ScanDtos.Task(
                task.getTaskNo(),
                task.getStatus(),
                task.getRiskScore(),
                task.getSeverity(),
                task.getRecommendation(),
                task.getSafeToInstall(),
                task.getPass(),
                task.getExecutionSuccessful(),
                completeness,
                task.getLlmUsed(),
                task.getScanMode(),
                findingsSummary,
                reportUrl,
                sarifUrl,
                task.getErrorCode(),
                task.getErrorMsg(),
                task.getMetadata(),
                task.getCreatedAt(),
                task.getStartedAt(),
                task.getFinishedAt());
    }

    public ScanDtos.Page<ScanDtos.Task> listTasks(int page, int size) {
        Page<ScanTaskEntity> result = taskRepo.findAllByOrderByCreatedAtDesc(PageRequest.of(page - 1, size));
        List<ScanDtos.Task> items = result.getContent().stream().map(this::toDto).toList();
        return new ScanDtos.Page<>(items, page, size, result.getTotalElements());
    }

    public ScanDtos.Page<Finding> listFindings(String taskNo, int page, int size,
                                               String severity, String ruleId, String category) {
        ScanTaskEntity task = requireTask(taskNo);
        Specification<ScanFindingEntity> spec = (root, query, cb) ->
                cb.equal(root.get("taskId"), task.getId());
        if (severity != null && !severity.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("severity"), severity.toUpperCase()));
        }
        if (ruleId != null && !ruleId.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("ruleId"), ruleId));
        }
        if (category != null && !category.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category"), category));
        }
        Page<ScanFindingEntity> result = findingRepo.findAll(spec, PageRequest.of(page - 1, size));
        List<Finding> items = result.getContent().stream().map(this::toFinding).toList();
        return new ScanDtos.Page<>(items, page, size, result.getTotalElements());
    }

    private Finding toFinding(ScanFindingEntity f) {
        return new Finding(
                f.getFindingId(),
                f.getRuleId(),
                f.getSeverity(),
                f.getCategory(),
                f.getPattern(),
                f.getFile(),
                f.getStartLine(),
                f.getEndLine(),
                f.getExplanation(),
                f.getExplanation(),
                f.getRemediation(),
                f.getConfidence(),
                f.getMatchedText(),
                f.getSourceUrl(),
                f.getTransitiveDepth());
    }

    public void retry(String taskNo) {
        stateService.resetForRetry(taskNo);
        queueService.enqueue(taskNo);
    }

    @Transactional
    public void cancel(String taskNo) {
        ScanTaskEntity task = requireTask(taskNo);
        String status = task.getStatus();
        if (ScanStatus.SUCCEEDED.name().equals(status)
                || ScanStatus.FAILED.name().equals(status)
                || ScanStatus.CANCELED.name().equals(status)) {
            return;
        }
        if (ScanStatus.QUEUED.name().equals(status)) {
            queueService.remove(taskNo);
        }
        task.setStatus(ScanStatus.CANCELED.name());
        task.setFinishedAt(Instant.now());
        taskRepo.save(task);
    }
}
