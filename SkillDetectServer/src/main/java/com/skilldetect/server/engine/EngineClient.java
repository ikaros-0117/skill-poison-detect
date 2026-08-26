package com.skilldetect.server.engine;

import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class EngineClient {

    private final RestClient restClient;
    private final EngineCircuitBreaker circuitBreaker;

    public EngineClient(RestClient engineRestClient, EngineCircuitBreaker circuitBreaker) {
        this.restClient = engineRestClient;
        this.circuitBreaker = circuitBreaker;
    }

    /** Synchronous scan. The engine holds the connection until the scan finishes. */
    public EngineScanResponse scan(String path, boolean useLlm, String outputFormat, String baselineContent) {
        if (!circuitBreaker.allowRequest()) {
            throw new EngineCircuitOpenException("engine circuit breaker is open");
        }
        Map<String, Object> body = new java.util.LinkedHashMap<>();
        body.put("path", path);
        body.put("use_llm", useLlm);
        body.put("output_format", outputFormat);
        if (baselineContent != null && !baselineContent.isBlank()) {
            body.put("baseline", baselineContent);
        }
        try {
            EngineScanResponse response = restClient.post()
                    .uri("/v1/scan")
                    .body(body)
                    .retrieve()
                    .body(EngineScanResponse.class);
            circuitBreaker.recordSuccess();
            return response;
        } catch (RestClientException ex) {
            circuitBreaker.recordFailure();
            throw ex;
        }
    }

    public Map<String, Object> health() {
        try {
            return restClient.get()
                    .uri("/health")
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});
        } catch (RestClientException ex) {
            return Map.of("status", "DOWN", "error", ex.getMessage());
        }
    }

    public Map<String, Object> healthDeep() {
        try {
            return restClient.get()
                    .uri("/health/deep?timeout=30")
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});
        } catch (RestClientException ex) {
            return Map.of("status", "DOWN", "error", ex.getMessage());
        }
    }

    public Map<String, Object> cancel(String engineScanId) {
        try {
            return restClient.post()
                    .uri(uriBuilder -> uriBuilder.path("/v1/scan/cancel").queryParam("scan_id", engineScanId).build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});
        } catch (RestClientException ex) {
            return Map.of("status", "ERROR", "error", ex.getMessage());
        }
    }
}
