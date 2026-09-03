package com.skilldetect.server.scan.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.skilldetect.server.config.ScanProperties;
import com.skilldetect.server.scan.domain.ScanStatus;
import com.skilldetect.server.scan.domain.ScanTaskEntity;
import com.skilldetect.server.scan.mapper.ScanTaskMapper;
import com.skilldetect.server.scan.queue.ScanQueueService;

@Component
public class ScanReconciler {

    private static final Logger log = LoggerFactory.getLogger(ScanReconciler.class);

    private final ScanTaskMapper taskMapper;
    private final ScanQueueService queueService;
    private final ScanStateService stateService;
    private final ScanProperties properties;

    public ScanReconciler(ScanTaskMapper taskMapper,
                          ScanQueueService queueService,
                          ScanStateService stateService,
                          ScanProperties properties) {
        this.taskMapper = taskMapper;
        this.queueService = queueService;
        this.stateService = stateService;
        this.properties = properties;
    }

    /** On startup, re-enqueue QUEUED tasks that are not already in the Redis queue. */
    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        List<ScanTaskEntity> queued = taskMapper.selectList(
                new LambdaQueryWrapper<ScanTaskEntity>()
                        .eq(ScanTaskEntity::getStatus, ScanStatus.QUEUED.name()));
        int reEnqueued = 0;
        for (ScanTaskEntity task : queued) {
            if (!queueService.isQueued(task.getTaskNo())) {
                queueService.enqueue(task.getTaskNo());
                reEnqueued++;
            }
        }
        log.info("startup reconcile: {} queued task(s), {} re-enqueued", queued.size(), reEnqueued);
    }

    /** Periodically re-enqueue QUEUED tasks that are not in the Redis queue (self-heal). */
    @Scheduled(fixedDelay = 10_000, initialDelay = 5_000)
    public void reconcileQueued() {
        List<ScanTaskEntity> queued = taskMapper.selectList(
                new LambdaQueryWrapper<ScanTaskEntity>()
                        .eq(ScanTaskEntity::getStatus, ScanStatus.QUEUED.name()));
        for (ScanTaskEntity task : queued) {
            if (!queueService.isQueued(task.getTaskNo())) {
                queueService.enqueue(task.getTaskNo());
            }
        }
    }

    /** Periodically mark RUNNING tasks that exceed the engine timeout as failed. */
    @Scheduled(fixedDelay = 30_000, initialDelay = 60_000)
    public void reconcileTimeouts() {
        List<ScanTaskEntity> running = taskMapper.selectList(
                new LambdaQueryWrapper<ScanTaskEntity>()
                        .eq(ScanTaskEntity::getStatus, ScanStatus.RUNNING.name()));
        long timeoutSeconds = properties.getEngine().getTimeoutSeconds() + 120L;
        LocalDateTime now = LocalDateTime.now();
        for (ScanTaskEntity task : running) {
            if (task.getStartedAt() == null) {
                continue;
            }
            if (Duration.between(task.getStartedAt(), now).getSeconds() > timeoutSeconds) {
                stateService.markFailed(task.getTaskNo(), "TIMEOUT", "scan exceeded engine timeout");
            }
        }
    }
}
