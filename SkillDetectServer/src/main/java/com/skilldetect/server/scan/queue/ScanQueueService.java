package com.skilldetect.server.scan.queue;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis List backed queue (P0). MySQL remains the system of record; the
 * queue only holds taskNo strings and is rebuildable from the DB.
 *
 * P1 can swap this to a Redis Stream without changing the public methods.
 */
@Component
public class ScanQueueService {

    static final String QUEUE_KEY = "skillscan:queue";
    static final String CANCEL_SET_KEY = "skillscan:cancelled";

    private final StringRedisTemplate redis;

    public ScanQueueService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public void enqueue(String taskNo) {
        redis.opsForList().leftPush(QUEUE_KEY, taskNo);
    }

    /** Blocking pop; returns null on timeout. */
    public String blockingPop(long timeoutSeconds) {
        return redis.opsForList().rightPop(QUEUE_KEY, timeoutSeconds, TimeUnit.SECONDS);
    }

    public void remove(String taskNo) {
        redis.opsForList().remove(QUEUE_KEY, 0, taskNo);
    }

    /** Whether the task is currently present in the queue list (queue is small: <=100). */
    public boolean isQueued(String taskNo) {
        List<String> items = redis.opsForList().range(QUEUE_KEY, 0, -1);
        return items != null && items.contains(taskNo);
    }

    public void markCancelled(String taskNo) {
        redis.opsForSet().add(CANCEL_SET_KEY, taskNo);
        redis.expire(CANCEL_SET_KEY, Duration.ofHours(24));
    }

    public boolean isCancelled(String taskNo) {
        return Boolean.TRUE.equals(redis.opsForSet().isMember(CANCEL_SET_KEY, taskNo));
    }

    public long queueSize() {
        Long size = redis.opsForList().size(QUEUE_KEY);
        return size == null ? 0 : size;
    }
}
