package com.skilldetect.server.scan.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.skilldetect.server.config.ScanProperties;
import com.skilldetect.server.scan.domain.ScanStatus;
import com.skilldetect.server.scan.domain.ScanTaskEntity;
import com.skilldetect.server.scan.repository.ScanFindingRepository;
import com.skilldetect.server.scan.repository.ScanTaskRepository;

/** Deletes finished tasks (and their findings/files) older than retention-days. */
@Component
public class ScanRetentionCleaner {

    private static final Logger log = LoggerFactory.getLogger(ScanRetentionCleaner.class);

    private final ScanTaskRepository taskRepo;
    private final ScanFindingRepository findingRepo;
    private final FileStorageService fileStorage;
    private final ScanProperties properties;

    public ScanRetentionCleaner(ScanTaskRepository taskRepo,
                                ScanFindingRepository findingRepo,
                                FileStorageService fileStorage,
                                ScanProperties properties) {
        this.taskRepo = taskRepo;
        this.findingRepo = findingRepo;
        this.fileStorage = fileStorage;
        this.properties = properties;
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanup() {
        Instant cutoff = Instant.now().minus(Duration.ofDays(properties.getRetentionDays()));
        List<ScanTaskEntity> old = taskRepo.findByFinishedAtBeforeAndStatusIn(
                cutoff,
                List.of(ScanStatus.SUCCEEDED.name(), ScanStatus.FAILED.name(), ScanStatus.CANCELED.name()));
        if (old.isEmpty()) {
            return;
        }
        List<Long> ids = old.stream().map(ScanTaskEntity::getId).toList();
        findingRepo.deleteByTaskIdIn(ids);
        taskRepo.deleteAllById(ids);
        for (ScanTaskEntity task : old) {
            fileStorage.deleteDir(task.getTaskNo());
        }
        log.info("retention cleanup: removed {} finished task(s) older than {} days", old.size(), properties.getRetentionDays());
    }
}
