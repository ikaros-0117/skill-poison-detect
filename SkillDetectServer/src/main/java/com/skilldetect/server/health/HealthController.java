package com.skilldetect.server.health;

import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skilldetect.server.common.ApiResponse;
import com.skilldetect.server.engine.EngineClient;
import com.skilldetect.server.scan.queue.ScanQueueService;

@RestController
public class HealthController {

    private final DataSource dataSource;
    private final EngineClient engineClient;
    private final ScanQueueService queueService;

    public HealthController(DataSource dataSource, EngineClient engineClient, ScanQueueService queueService) {
        this.dataSource = dataSource;
        this.engineClient = engineClient;
        this.queueService = queueService;
    }

    @GetMapping("/healthz")
    public ResponseEntity<Map<String, Object>> liveness() {
        Map<String, Object> checks = new LinkedHashMap<>();
        checks.put("process", "UP");
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", "UP");
        body.put("checks", checks);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/readyz")
    public ResponseEntity<Map<String, Object>> readiness() {
        Map<String, Object> checks = new LinkedHashMap<>();
        boolean dbOk = checkDb();
        Map<String, Object> engineHealth = engineClient.health();
        boolean engineOk = "UP".equalsIgnoreCase(String.valueOf(engineHealth.get("status")));
        checks.put("db", dbOk ? "UP" : "DOWN");
        checks.put("engine", engineOk ? "UP" : "DOWN");

        boolean ready = dbOk && engineOk;
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", ready ? "UP" : "DOWN");
        body.put("checks", checks);
        return ready
                ? ResponseEntity.ok(body)
                : ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }

    @GetMapping("/api/v1/engine/health")
    public ApiResponse<Map<String, Object>> engineHealth(@RequestParam(defaultValue = "false") boolean deep) {
        Map<String, Object> health = deep ? engineClient.healthDeep() : engineClient.health();
        if (!health.containsKey("status")) {
            health.put("status", "DOWN");
        }
        if (!health.containsKey("version")) {
            health.put("version", "unknown");
        }
        health.put("queueDepth", queueService.queueSize());
        return ApiResponse.ok(health);
    }

    private boolean checkDb() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(2);
        } catch (Exception ex) {
            return false;
        }
    }
}
