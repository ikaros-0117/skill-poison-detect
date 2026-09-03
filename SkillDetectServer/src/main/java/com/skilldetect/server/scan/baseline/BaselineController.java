package com.skilldetect.server.scan.baseline;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.skilldetect.server.common.ApiResponse;
import com.skilldetect.server.common.BusinessException;
import com.skilldetect.server.scan.domain.ScanBaselineEntity;
import com.skilldetect.server.scan.mapper.ScanBaselineMapper;

@RestController
@RequestMapping("/api/v1/baselines")
public class BaselineController {

    private final ScanBaselineMapper baselineMapper;

    public BaselineController(ScanBaselineMapper baselineMapper) {
        this.baselineMapper = baselineMapper;
    }

    public static class CreateRequest {
        @NotBlank
        private String name;
        @NotBlank
        private String content;
        private String format;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getFormat() { return format; }
        public void setFormat(String format) { this.format = format; }
    }

    public static class BaselineView {
        private final Long baselineId;
        private final String name;
        private final String format;
        private final String version;
        private final LocalDateTime createdAt;

        public BaselineView(Long baselineId, String name, String format, String version, LocalDateTime createdAt) {
            this.baselineId = baselineId;
            this.name = name;
            this.format = format;
            this.version = version;
            this.createdAt = createdAt;
        }

        static BaselineView from(ScanBaselineEntity e) {
            return new BaselineView(e.getId(), e.getName(), e.getFormat(), e.getVersion(), e.getCreatedAt());
        }

        public Long getBaselineId() { return baselineId; }
        public String getName() { return name; }
        public String getFormat() { return format; }
        public String getVersion() { return version; }
        public LocalDateTime getCreatedAt() { return createdAt; }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BaselineView>> create(@RequestBody CreateRequest request) {
        ScanBaselineEntity entity = new ScanBaselineEntity();
        entity.setName(request.getName());
        entity.setContent(request.getContent());
        entity.setFormat(request.getFormat() == null || request.getFormat().trim().isEmpty() ? "yaml" : request.getFormat());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        baselineMapper.insert(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(BaselineView.from(entity)));
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> list() {
        List<BaselineView> items = baselineMapper.selectList(
                        new LambdaQueryWrapper<ScanBaselineEntity>()
                                .orderByDesc(ScanBaselineEntity::getCreatedAt))
                .stream().map(BaselineView::from).collect(Collectors.toList());
        return ApiResponse.ok(Collections.<String, Object>singletonMap("items", items));
    }

    @GetMapping("/{baselineId}")
    public ApiResponse<Map<String, Object>> get(@PathVariable Long baselineId) {
        ScanBaselineEntity e = baselineMapper.selectById(baselineId);
        if (e == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, 40404, "基线不存在");
        }
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("baselineId", e.getId());
        body.put("name", e.getName());
        body.put("content", e.getContent());
        body.put("format", e.getFormat());
        body.put("version", e.getVersion());
        body.put("createdAt", e.getCreatedAt());
        return ApiResponse.ok(body);
    }
}
