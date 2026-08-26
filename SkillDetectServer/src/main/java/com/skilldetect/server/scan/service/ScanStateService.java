package com.skilldetect.server.scan.service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import com.skilldetect.server.common.BusinessException;
import com.skilldetect.server.config.ScanProperties;
import com.skilldetect.server.engine.EngineScanResponse;
import com.skilldetect.server.scan.domain.ScanFindingEntity;
import com.skilldetect.server.scan.domain.ScanStatus;
import com.skilldetect.server.scan.domain.ScanTaskEntity;
import com.skilldetect.server.scan.repository.ScanFindingRepository;
import com.skilldetect.server.scan.repository.ScanTaskRepository;

@Service
public class ScanStateService {

    private final ScanTaskRepository taskRepo;
    private final ScanFindingRepository findingRepo;
    private final ScanProperties properties;

    public ScanStateService(ScanTaskRepository taskRepo,
                            ScanFindingRepository findingRepo,
                            ScanProperties properties) {
        this.taskRepo = taskRepo;
        this.findingRepo = findingRepo;
        this.properties = properties;
    }

    @Transactional
    public boolean claim(String taskNo) {
        return taskRepo.claimForDispatch(taskNo, Instant.now()) > 0;
    }

    @Transactional
    public void markCancelled(String taskNo) {
        taskRepo.findByTaskNo(taskNo).ifPresent(task -> {
            task.setStatus(ScanStatus.CANCELED.name());
            task.setFinishedAt(Instant.now());
            taskRepo.save(task);
        });
    }

    @Transactional
    public void markFailed(String taskNo, String errorCode, String errorMsg) {
        taskRepo.findByTaskNo(taskNo).ifPresent(task -> {
            if (!ScanStatus.RUNNING.name().equals(task.getStatus())) {
                return;
            }
            task.setStatus(ScanStatus.FAILED.name());
            task.setErrorCode(errorCode);
            task.setErrorMsg(errorMsg);
            task.setFinishedAt(Instant.now());
            taskRepo.save(task);
        });
    }

    @Transactional
    public ScanTaskEntity saveResult(String taskNo, EngineScanResponse result, String reportPath) {
        ScanTaskEntity task = taskRepo.findByTaskNo(taskNo).orElseThrow();
        if (!ScanStatus.RUNNING.name().equals(task.getStatus())) {
            return task; // cancelled or timed out by reconciler; ignore late result
        }
        int threshold = task.getRiskThreshold() != null ? task.getRiskThreshold() : properties.getRiskThreshold();
        task.setRiskScore(result.getRiskScore());
        task.setSeverity(result.getSeverity());
        task.setRecommendation(result.getRecommendation());
        task.setSafeToInstall(result.getSafeToInstall());
        task.setExecutionSuccessful(result.getExecutionSuccessful());
        task.setLlmUsed(result.getLlmUsed());
        task.setScanMode(result.getScanMode());
        task.setEngineScanId(result.getEngineScanId());

        Map<String, Object> completeness = result.getAnalysisCompleteness();
        if (completeness != null) {
            task.setAnalysisComplete(asBoolean(completeness.get("is_complete")));
            task.setEntirelyUninspectedFiles(asInt(completeness.get("entirely_uninspected_files")));
        }

        boolean pass = result.getRiskScore() != null
                && result.getRiskScore() <= threshold
                && Boolean.TRUE.equals(result.getExecutionSuccessful())
                && Boolean.TRUE.equals(task.getAnalysisComplete())
                && (task.getEntirelyUninspectedFiles() == null || task.getEntirelyUninspectedFiles() == 0);
        task.setPass(pass);
        task.setReportPath(reportPath);
        task.setStatus(ScanStatus.SUCCEEDED.name());
        task.setFinishedAt(Instant.now());
        return taskRepo.save(task);
    }

    @Transactional
    public ScanTaskEntity saveNewTask(ScanTaskEntity task) {
        return taskRepo.save(task);
    }
    @Transactional
    public void resetForRetry(String taskNo) {
        ScanTaskEntity task = taskRepo.findByTaskNo(taskNo).orElseThrow();
        if (!ScanStatus.FAILED.name().equals(task.getStatus())
                && !ScanStatus.CANCELED.name().equals(task.getStatus())) {
            throw new BusinessException(HttpStatus.CONFLICT, 40902, "仅 FAILED/CANCELED 任务可重试");
        }
        task.setStatus(ScanStatus.QUEUED.name());
        task.setRiskScore(null);
        task.setSeverity(null);
        task.setRecommendation(null);
        task.setSafeToInstall(null);
        task.setPass(null);
        task.setExecutionSuccessful(null);
        task.setAnalysisComplete(null);
        task.setEntirelyUninspectedFiles(null);
        task.setLlmUsed(null);
        task.setScanMode(null);
        task.setEngineScanId(null);
        task.setErrorCode(null);
        task.setErrorMsg(null);
        task.setStartedAt(null);
        task.setFinishedAt(null);
        task.setRetryCount(task.getRetryCount() + 1);
        taskRepo.save(task);
    }


    @Transactional
    public void saveFindings(Long taskId, List<ScanFindingEntity> findings) {
        if (findings != null && !findings.isEmpty()) {
            findingRepo.saveAll(findings);
        }
    }

    private static Boolean asBoolean(Object value) {
        if (value instanceof Boolean b) {
            return b;
        }
        if (value instanceof String s) {
            return Boolean.parseBoolean(s);
        }
        return null;
    }

    private static Integer asInt(Object value) {
        if (value instanceof Number n) {
            return n.intValue();
        }
        if (value instanceof String s && !s.isBlank()) {
            return Integer.parseInt(s);
        }
        return null;
    }
}
