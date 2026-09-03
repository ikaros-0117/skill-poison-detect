package com.skilldetect.server.health;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.skilldetect.server.health.mapper.EngineHealthLogMapper;

/** Deletes engine health probe history older than the retention window. */
@Component
public class EngineHealthLogCleaner {

    private static final Logger log = LoggerFactory.getLogger(EngineHealthLogCleaner.class);
    private static final int RETENTION_DAYS = 30;

    private final EngineHealthLogMapper logMapper;

    public EngineHealthLogCleaner(EngineHealthLogMapper logMapper) {
        this.logMapper = logMapper;
    }

    @Scheduled(cron = "0 20 3 * * *")
    public void cleanup() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(RETENTION_DAYS);
        int deleted = logMapper.delete(new LambdaQueryWrapper<EngineHealthLogEntity>()
                .lt(EngineHealthLogEntity::getCheckedAt, cutoff));
        if (deleted > 0) {
            log.info("engine health log cleanup: removed {} record(s) older than {} days",
                    deleted, RETENTION_DAYS);
        }
    }
}
