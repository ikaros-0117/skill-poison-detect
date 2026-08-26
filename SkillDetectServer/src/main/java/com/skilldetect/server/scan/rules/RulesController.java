package com.skilldetect.server.scan.rules;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skilldetect.server.common.ApiResponse;

/**
 * Rule catalog endpoint. Serves the curated rule inventory loaded from
 * {@code rules.json} (generated from SkillSpector's documented vulnerability
 * pattern tables).
 */
@RestController
@RequestMapping("/api/v1")
public class RulesController {

    private final Map<String, Object> catalog;

    public RulesController(ObjectMapper objectMapper) throws IOException {
        try (InputStream in = new ClassPathResource("rules.json").getInputStream()) {
            this.catalog = objectMapper.readValue(in, new TypeReference<Map<String, Object>>() {});
        }
    }

    @GetMapping("/rules")
    public ApiResponse<Map<String, Object>> rules() {
        return ApiResponse.ok(catalog);
    }
}
