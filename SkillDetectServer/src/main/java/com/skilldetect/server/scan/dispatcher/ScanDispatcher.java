package com.skilldetect.server.scan.dispatcher;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.skilldetect.server.config.ScanProperties;
import com.skilldetect.server.scan.queue.ScanQueueService;
import com.skilldetect.server.scan.service.ScanExecutionService;

/**
 * Bounded-parallel dispatcher: max-active worker threads each do a blocking
 * pop from the Redis queue and run one scan at a time. Concurrency == thread
 * count (default 8); the remaining tasks stay queued.
 */
@Component
public class ScanDispatcher {

    private static final Logger log = LoggerFactory.getLogger(ScanDispatcher.class);

    private final ScanQueueService queueService;
    private final ScanExecutionService executionService;
    private final ScanProperties properties;

    private final List<Thread> workers = new ArrayList<>();
    private volatile boolean running = true;

    public ScanDispatcher(ScanQueueService queueService,
                          ScanExecutionService executionService,
                          ScanProperties properties) {
        this.queueService = queueService;
        this.executionService = executionService;
        this.properties = properties;
    }

    @PostConstruct
    public void start() {
        int count = Math.max(1, properties.getConcurrency().getMaxActive());
        for (int i = 0; i < count; i++) {
            Thread thread = new Thread(this::loop, "scan-dispatcher-" + i);
            thread.setDaemon(true);
            thread.start();
            workers.add(thread);
        }
        log.info("scan dispatcher started with {} worker threads", count);
    }

    private void loop() {
        while (running) {
            try {
                String taskNo = queueService.blockingPop(5);
                if (taskNo != null) {
                    executionService.execute(taskNo);
                }
            } catch (Exception ex) {
                if (running) {
                    log.warn("dispatcher loop error", ex);
                }
            }
        }
    }

    @PreDestroy
    public void stop() {
        running = false;
        for (Thread thread : workers) {
            thread.interrupt();
        }
    }
}
