package com.skilldetect.server.scan.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.skilldetect.server.config.ScanProperties;
import com.skilldetect.server.scan.domain.ScanFindingEntity;
import com.skilldetect.server.scan.domain.ScanStatus;
import com.skilldetect.server.scan.domain.ScanTaskEntity;
import com.skilldetect.server.scan.mapper.ScanFindingMapper;
import com.skilldetect.server.scan.mapper.ScanTaskMapper;

/** Deletes finished tasks (and their findings/files) older than retention-days. */
@Component
public class ScanRetentionCleaner {

    private static final Logger log = LoggerFactory.getLogger(ScanRetentionCleaner.class);

    private final ScanTaskMapper taskMapper;
    private final ScanFindingMapper findingMapper;
    private final FileStorageService fileStorage;
    private final ScanProperties properties;

    public ScanRetentionCleaner(ScanTaskMapper taskMapper,
                                ScanFindingMapper findingMapper,
                                FileStorageService fileStorage,
                                ScanProperties properties) {
        this.taskMapper = taskMapper;
        this.findingMapper = findingMapper;
        this.fileStorage = fileStorage;
        this.properties = properties;
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanup() {
        LocalDateTime cutoff = LocalDateTime.now().minus(Duration.ofDays(properties.getRetentionDays()));
        List<ScanTaskEntity> old = taskMapper.selectList(
                new LambdaQueryWrapper<ScanTaskEntity>()
                        .lt(ScanTaskEntity::getFinishedAt, cutoff)
                        .in(ScanTaskEntity::getStatus, Arrays.asList(
                                ScanStatus.SUCCEEDED.name(),
                                ScanStatus.FAILED.name(),
                                ScanStatus.CANCELED.name())));
        if (old.isEmpty()) {
            return;
        }
        List<Long> ids = old.stream().map(ScanTaskEntity::getId).collect(Collectors.toList());
        findingMapper.delete(new LambdaQueryWrapper<ScanFindingEntity>()
                .in(ScanFindingEntity::getTaskId, ids));
        taskMapper.deleteBatchIds(ids);
        for (ScanTaskEntity task : old) {
            fileStorage.deleteDir(task.getTaskNo());
        }
        log.info("retention cleanup: removed {} finished task(s) older than {} days", old.size(), properties.getRetentionDays());
    }
}
