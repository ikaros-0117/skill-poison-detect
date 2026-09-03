package com.skilldetect.server.scan.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.skilldetect.server.common.BusinessException;
import com.skilldetect.server.config.ScanProperties;
import com.skilldetect.server.engine.EngineScanResponse;
import com.skilldetect.server.scan.domain.ScanFindingEntity;
import com.skilldetect.server.scan.domain.ScanStatus;
import com.skilldetect.server.scan.domain.ScanTaskEntity;
import com.skilldetect.server.scan.mapper.ScanFindingMapper;
import com.skilldetect.server.scan.mapper.ScanTaskMapper;

@Service
public class ScanStateService {

    private final ScanTaskMapper taskMapper;
    private final ScanFindingMapper findingMapper;
    private final ScanProperties properties;

    public ScanStateService(ScanTaskMapper taskMapper,
                            ScanFindingMapper findingMapper,
                            ScanProperties properties) {
        this.taskMapper = taskMapper;
        this.findingMapper = findingMapper;
        this.properties = properties;
    }

    @Transactional
    public boolean claim(String taskNo) {
        LambdaUpdateWrapper<ScanTaskEntity> update = new LambdaUpdateWrapper<>();
        update.eq(ScanTaskEntity::getTaskNo, taskNo)
                .eq(ScanTaskEntity::getStatus, ScanStatus.QUEUED.name())
                .set(ScanTaskEntity::getStatus, ScanStatus.RUNNING.name())
                .set(ScanTaskEntity::getStartedAt, LocalDateTime.now());
        return taskMapper.update(null, update) > 0;
    }

    @Transactional
    public void markCancelled(String taskNo) {
        findByTaskNo(taskNo).ifPresent(task -> {
            task.setStatus(ScanStatus.CANCELED.name());
            task.setFinishedAt(LocalDateTime.now());
            taskMapper.updateById(task);
        });
    }

    @Transactional
    public void markFailed(String taskNo, String errorCode, String errorMsg) {
        findByTaskNo(taskNo).ifPresent(task -> {
            if (!ScanStatus.RUNNING.name().equals(task.getStatus())) {
                return;
            }
            task.setStatus(ScanStatus.FAILED.name());
            task.setErrorCode(errorCode);
            task.setErrorMsg(errorMsg);
            task.setFinishedAt(LocalDateTime.now());
            taskMapper.updateById(task);
        });
    }

    @Transactional
    public void completeSuccess(String taskNo, EngineScanResponse result, String reportPath,
                                List<ScanFindingEntity> findings) {
        ScanTaskEntity task = findByTaskNo(taskNo).orElseThrow(
                () -> new BusinessException(HttpStatus.NOT_FOUND, 40401, "任务不存在: " + taskNo));
        if (!ScanStatus.RUNNING.name().equals(task.getStatus())) {
            return; // cancelled or timed out by reconciler; ignore late result
        }

        int threshold = task.getRiskThreshold() != null ? task.getRiskThreshold() : properties.getRiskThreshold();
        Boolean analysisComplete = null;
        Integer entirelyUninspectedFiles = null;
        Map<String, Object> completeness = result.getAnalysisCompleteness();
        if (completeness != null) {
            analysisComplete = asBoolean(completeness.get("is_complete"));
            entirelyUninspectedFiles = asInt(completeness.get("entirely_uninspected_files"));
        }

        boolean pass = result.getRiskScore() != null
                && result.getRiskScore() <= threshold
                && Boolean.TRUE.equals(result.getExecutionSuccessful())
                && Boolean.TRUE.equals(analysisComplete)
                && (entirelyUninspectedFiles == null || entirelyUninspectedFiles == 0);

        LambdaUpdateWrapper<ScanTaskEntity> update = new LambdaUpdateWrapper<>();
        update.eq(ScanTaskEntity::getTaskNo, taskNo)
                .eq(ScanTaskEntity::getStatus, ScanStatus.RUNNING.name())
                .set(ScanTaskEntity::getStatus, ScanStatus.SUCCEEDED.name())
                .set(ScanTaskEntity::getRiskScore, result.getRiskScore())
                .set(ScanTaskEntity::getSeverity, result.getSeverity())
                .set(ScanTaskEntity::getRecommendation, result.getRecommendation())
                .set(ScanTaskEntity::getSafeToInstall, result.getSafeToInstall())
                .set(ScanTaskEntity::getPass, pass)
                .set(ScanTaskEntity::getExecutionSuccessful, result.getExecutionSuccessful())
                .set(ScanTaskEntity::getLlmUsed, result.getLlmUsed())
                .set(ScanTaskEntity::getScanMode, result.getScanMode())
                .set(ScanTaskEntity::getEngineScanId, result.getEngineScanId())
                .set(ScanTaskEntity::getAnalysisComplete, analysisComplete)
                .set(ScanTaskEntity::getEntirelyUninspectedFiles, entirelyUninspectedFiles)
                .set(ScanTaskEntity::getReportPath, reportPath)
                .set(ScanTaskEntity::getErrorCode, null)
                .set(ScanTaskEntity::getErrorMsg, null)
                .set(ScanTaskEntity::getFinishedAt, LocalDateTime.now());
        int updated = taskMapper.update(null, update);
        if (updated == 0) {
            return; // lost race with cancel/timeout; ignore late result
        }

        if (findings != null && !findings.isEmpty()) {
            for (ScanFindingEntity finding : findings) {
                finding.setTaskId(task.getId());
                findingMapper.insert(finding);
            }
        }
    }

    @Transactional
    public ScanTaskEntity saveNewTask(ScanTaskEntity task) {
        taskMapper.insert(task);
        return task;
    }

    @Transactional
    public void resetForRetry(String taskNo) {
        ScanTaskEntity task = findByTaskNo(taskNo).orElseThrow(
                () -> new BusinessException(HttpStatus.NOT_FOUND, 40401, "任务不存在: " + taskNo));
        if (!ScanStatus.FAILED.name().equals(task.getStatus())
                && !ScanStatus.CANCELED.name().equals(task.getStatus())) {
            throw new BusinessException(HttpStatus.CONFLICT, 40902, "仅 FAILED/CANCELED 任务可重试");
        }
        LambdaUpdateWrapper<ScanTaskEntity> update = new LambdaUpdateWrapper<>();
        update.eq(ScanTaskEntity::getTaskNo, taskNo)
                .set(ScanTaskEntity::getStatus, ScanStatus.QUEUED.name())
                .set(ScanTaskEntity::getRiskScore, null)
                .set(ScanTaskEntity::getSeverity, null)
                .set(ScanTaskEntity::getRecommendation, null)
                .set(ScanTaskEntity::getSafeToInstall, null)
                .set(ScanTaskEntity::getPass, null)
                .set(ScanTaskEntity::getExecutionSuccessful, null)
                .set(ScanTaskEntity::getAnalysisComplete, null)
                .set(ScanTaskEntity::getEntirelyUninspectedFiles, null)
                .set(ScanTaskEntity::getLlmUsed, null)
                .set(ScanTaskEntity::getScanMode, null)
                .set(ScanTaskEntity::getEngineScanId, null)
                .set(ScanTaskEntity::getErrorCode, null)
                .set(ScanTaskEntity::getErrorMsg, null)
                .set(ScanTaskEntity::getStartedAt, null)
                .set(ScanTaskEntity::getFinishedAt, null)
                .set(ScanTaskEntity::getRetryCount, task.getRetryCount() + 1);
        taskMapper.update(null, update);
    }


    private Optional<ScanTaskEntity> findByTaskNo(String taskNo) {
        return Optional.ofNullable(taskMapper.selectOne(
                new LambdaQueryWrapper<ScanTaskEntity>().eq(ScanTaskEntity::getTaskNo, taskNo)));
    }

    private static Boolean asBoolean(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return null;
    }

    private static Integer asInt(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            String s = (String) value;
            if (s != null && !s.trim().isEmpty()) {
                return Integer.parseInt(s);
            }
        }
        return null;
    }
}
