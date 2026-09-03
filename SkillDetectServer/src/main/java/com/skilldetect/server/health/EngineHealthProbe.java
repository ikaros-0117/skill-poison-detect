package com.skilldetect.server.health;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.skilldetect.server.engine.EngineClient;
import com.skilldetect.server.health.mapper.EngineHealthLogMapper;
import com.skilldetect.server.scan.queue.ScanQueueService;

/** Periodically runs the engine deep-health probe and records the result. */
@Component
public class EngineHealthProbe {

    private static final Logger log = LoggerFactory.getLogger(EngineHealthProbe.class);

    private final EngineClient engineClient;
    private final EngineHealthLogMapper logMapper;
    private final ScanQueueService queueService;

    public EngineHealthProbe(EngineClient engineClient,
                             EngineHealthLogMapper logMapper,
                             ScanQueueService queueService) {
        this.engineClient = engineClient;
        this.logMapper = logMapper;
        this.queueService = queueService;
    }

    @Scheduled(fixedDelay = 60_000, initialDelay = 30_000)
    public void probe() {
        Map<String, Object> deep = engineClient.healthDeep();
        Map<String, Object> basic = engineClient.health();

        EngineHealthLogEntity entry = new EngineHealthLogEntity();
        entry.setEngineVersion(asString(basic.get("version")));
        entry.setProvider(asString(basic.get("provider")));
        entry.setLlmAvailable(asBoolean(basic.get("llm_available")));
        entry.setActiveScans(asInt(basic.get("active_scans")));
        entry.setQueueDepth((int) queueService.queueSize());
        entry.setLatencyMs(asInt(deep.get("elapsed_ms")));
        entry.setStatus(asString(deep.get("status")));
        logMapper.insert(entry);

        if (!"UP".equalsIgnoreCase(entry.getStatus())) {
            log.warn("engine deep health probe status={} error={}", entry.getStatus(), deep.get("error"));
        }
    }

    private static String asString(Object v) { return v == null ? null : String.valueOf(v); }
    private static Boolean asBoolean(Object v) { return v instanceof Boolean ? (Boolean) v : null; }
    private static Integer asInt(Object v) { return v instanceof Number ? ((Number) v).intValue() : null; }
}
