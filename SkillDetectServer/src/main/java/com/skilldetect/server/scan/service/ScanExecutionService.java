package com.skilldetect.server.scan.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.skilldetect.server.engine.EngineClient;
import com.skilldetect.server.engine.EngineScanResponse;
import com.skilldetect.server.scan.domain.ScanFindingEntity;
import com.skilldetect.server.scan.domain.ScanStatus;
import com.skilldetect.server.scan.domain.ScanBaselineEntity;
import com.skilldetect.server.scan.domain.ScanTaskEntity;
import com.skilldetect.server.scan.mapper.ScanBaselineMapper;
import com.skilldetect.server.scan.mapper.ScanTaskMapper;
import com.skilldetect.server.scan.queue.ScanQueueService;

@Service
public class ScanExecutionService {

    private static final Logger log = LoggerFactory.getLogger(ScanExecutionService.class);

    private final ScanTaskMapper taskMapper;
    private final ScanStateService stateService;
    private final ScanQueueService queueService;
    private final FileStorageService fileStorage;
    private final EngineClient engineClient;
    private final ScanBaselineMapper baselineMapper;

    public ScanExecutionService(ScanTaskMapper taskMapper,
                                ScanStateService stateService,
                                ScanQueueService queueService,
                                FileStorageService fileStorage,
                                EngineClient engineClient,
                                ScanBaselineMapper baselineMapper) {
        this.taskMapper = taskMapper;
        this.stateService = stateService;
        this.queueService = queueService;
        this.fileStorage = fileStorage;
        this.engineClient = engineClient;
        this.baselineMapper = baselineMapper;
    }

    public void execute(String taskNo) {
        ScanTaskEntity task = taskMapper.selectOne(
                new LambdaQueryWrapper<ScanTaskEntity>().eq(ScanTaskEntity::getTaskNo, taskNo));
        if (task == null || !ScanStatus.QUEUED.name().equals(task.getStatus())) {
            return;
        }
        if (queueService.isCancelled(taskNo)) {
            stateService.markCancelled(taskNo);
            return;
        }
        if (!stateService.claim(taskNo)) {
            return; // another worker already claimed it
        }

        ScanTaskEntity running = taskMapper.selectOne(
                new LambdaQueryWrapper<ScanTaskEntity>().eq(ScanTaskEntity::getTaskNo, taskNo));
        if (running == null) {
            return;
        }
        String format = "sarif".equalsIgnoreCase(running.getReportFormat()) ? "sarif" : "json";
        try {
            String baselineContent = null;
            if (running.getBaselineId() != null) {
                ScanBaselineEntity baseline = baselineMapper.selectById(running.getBaselineId());
                baselineContent = baseline != null ? baseline.getContent() : null;
            }
            EngineScanResponse result = engineClient.scan(
                    running.getSourcePath(), running.isUseLlm(), format, baselineContent);

            String reportPath = fileStorage.writeReport(taskNo, result.getReport(), format);
            stateService.completeSuccess(taskNo, result, reportPath, mapFindings(result));
        } catch (Exception ex) {
            log.warn("scan failed taskNo={}: {}", taskNo, ex.getMessage());
            stateService.markFailed(taskNo, "ENGINE_ERROR", ex.getMessage());
        }
    }

    private List<ScanFindingEntity> mapFindings(EngineScanResponse result) {
        List<ScanFindingEntity> entities = new ArrayList<>();
        if (result.getFindings() == null) {
            return entities;
        }
        for (Map<String, Object> f : result.getFindings()) {
            ScanFindingEntity e = new ScanFindingEntity();
            e.setFindingId(asString(f.get("finding_id")));
            e.setRuleId(asString(f.get("id")));
            e.setSeverity(asString(f.get("severity")));
            e.setCategory(asString(f.get("category")));
            e.setPattern(asString(f.get("pattern")));
            e.setExplanation(asString(f.get("explanation")));
            e.setRemediation(asString(f.get("remediation")));
            e.setConfidence(asDouble(f.get("confidence")));
            e.setMatchedText(asString(f.get("finding")));
            e.setFingerprint(asString(f.get("match_fingerprint")));
            e.setSourceUrl(asString(f.get("source_url")));
            e.setTransitiveDepth(asInt(f.get("transitive_depth")));

            Object location = f.get("location");
            if (location instanceof Map) {
                Map<?, ?> loc = (Map<?, ?>) location;
                e.setFile(asString(loc.get("file")));
                e.setStartLine(asInt(loc.get("start_line")));
                e.setEndLine(asInt(loc.get("end_line")));
            }
            entities.add(e);
        }
        return entities;
    }

    private static String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private static Double asDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            String s = (String) value;
            if (s != null && !s.trim().isEmpty()) {
                try {
                    return Double.parseDouble(s);
                } catch (NumberFormatException ignored) {
                    return null;
                }
            }
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
                try {
                    return Integer.parseInt(s);
                } catch (NumberFormatException ignored) {
                    return null;
                }
            }
        }
        return null;
    }
}
