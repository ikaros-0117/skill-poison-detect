package com.skilldetect.server.engine;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class EngineClient {

    private final RestTemplate restTemplate;
    private final EngineCircuitBreaker circuitBreaker;

    public EngineClient(RestTemplate engineRestTemplate, EngineCircuitBreaker circuitBreaker) {
        this.restTemplate = engineRestTemplate;
        this.circuitBreaker = circuitBreaker;
    }

    /** Synchronous scan. The engine holds the connection until the scan finishes. */
    public EngineScanResponse scan(String path, boolean useLlm, String outputFormat, String baselineContent) {
        if (!circuitBreaker.allowRequest()) {
            throw new EngineCircuitOpenException("engine circuit breaker is open");
        }
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("path", path);
        body.put("use_llm", useLlm);
        body.put("output_format", outputFormat);
        if (baselineContent != null && !baselineContent.trim().isEmpty()) {
            body.put("baseline", baselineContent);
        }
        try {
            ResponseEntity<EngineScanResponse> response =
                    restTemplate.postForEntity("/v1/scan", body, EngineScanResponse.class);
            circuitBreaker.recordSuccess();
            return response.getBody();
        } catch (RestClientException ex) {
            circuitBreaker.recordFailure();
            throw ex;
        }
    }

    public Map<String, Object> health() {
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    "/health",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {});
            return response.getBody();
        } catch (RestClientException ex) {
            return errorMap("DOWN", ex.getMessage());
        }
    }

    public Map<String, Object> healthDeep() {
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    "/health/deep?timeout=30",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {});
            return response.getBody();
        } catch (RestClientException ex) {
            return errorMap("DOWN", ex.getMessage());
        }
    }

    public Map<String, Object> cancel(String engineScanId) {
        try {
            String uri = UriComponentsBuilder.fromPath("/v1/scan/cancel")
                    .queryParam("scan_id", engineScanId)
                    .toUriString();
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    uri,
                    HttpMethod.POST,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {});
            return response.getBody();
        } catch (RestClientException ex) {
            return errorMap("ERROR", ex.getMessage());
        }
    }

    private Map<String, Object> errorMap(String status, String error) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("status", status);
        map.put("error", error);
        return map;
    }
}
