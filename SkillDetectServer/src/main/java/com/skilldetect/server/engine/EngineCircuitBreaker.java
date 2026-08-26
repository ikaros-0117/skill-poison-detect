package com.skilldetect.server.engine;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

import com.skilldetect.server.config.ScanProperties;

/** Lightweight circuit breaker guarding calls to the scanning engine. */
@Component
public class EngineCircuitBreaker {

    private final int failureThreshold;
    private final long openMillis;
    private final AtomicInteger consecutiveFailures = new AtomicInteger(0);
    private volatile long openedUntil = 0L;

    public EngineCircuitBreaker(ScanProperties properties) {
        this.failureThreshold = Math.max(1, properties.getEngine().getCircuitFailureThreshold());
        this.openMillis = properties.getEngine().getCircuitOpenSeconds() * 1000L;
    }

    public boolean allowRequest() {
        if (openedUntil == 0L) {
            return true;
        }
        if (System.currentTimeMillis() > openedUntil) {
            // Half-open: allow one probe request through.
            openedUntil = 0L;
            consecutiveFailures.set(0);
            return true;
        }
        return false;
    }

    public void recordSuccess() {
        consecutiveFailures.set(0);
        openedUntil = 0L;
    }

    public void recordFailure() {
        if (consecutiveFailures.incrementAndGet() >= failureThreshold) {
            openedUntil = System.currentTimeMillis() + openMillis;
        }
    }

    public boolean isOpen() {
        return openedUntil != 0L && System.currentTimeMillis() <= openedUntil;
    }
}
