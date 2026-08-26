package com.skilldetect.server.config;

import org.springframework.stereotype.Component;

import com.skilldetect.server.scan.queue.ScanQueueService;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

@Component
public class ScanMetrics {

    public ScanMetrics(MeterRegistry registry, ScanQueueService queueService) {
        Gauge.builder("skillscan.queue.depth", queueService, s -> (double) s.queueSize())
                .description("Number of tasks waiting in the Redis scan queue")
                .register(registry);
    }
}
