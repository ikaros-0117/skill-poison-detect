package com.skilldetect.server.scan.baseline;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skilldetect.server.common.ApiResponse;
import com.skilldetect.server.common.BusinessException;
import com.skilldetect.server.scan.domain.ScanBaselineEntity;
import com.skilldetect.server.scan.repository.ScanBaselineRepository;

import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/v1/baselines")
public class BaselineController {

    private final ScanBaselineRepository baselineRepo;

    public BaselineController(ScanBaselineRepository baselineRepo) {
        this.baselineRepo = baselineRepo;
    }

    public record CreateRequest(@NotBlank String name, @NotBlank String content, String format) {}

    public record BaselineView(Long baselineId, String name, String format, String version, Instant createdAt) {
        static BaselineView from(ScanBaselineEntity e) {
            return new BaselineView(e.getId(), e.getName(), e.getFormat(), e.getVersion(), e.getCreatedAt());
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BaselineView>> create(@RequestBody CreateRequest request) {
        ScanBaselineEntity entity = new ScanBaselineEntity();
        entity.setName(request.name());
        entity.setContent(request.content());
        entity.setFormat(request.format() == null || request.format().isBlank() ? "yaml" : request.format());
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        ScanBaselineEntity saved = baselineRepo.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(BaselineView.from(saved)));
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> list() {
        List<BaselineView> items = baselineRepo.findAllByOrderByCreatedAtDesc().stream().map(BaselineView::from).toList();
        return ApiResponse.ok(Map.of("items", items));
    }

    @GetMapping("/{baselineId}")
    public ApiResponse<Map<String, Object>> get(@PathVariable Long baselineId) {
        ScanBaselineEntity e = baselineRepo.findById(baselineId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, 40404, "基线不存在"));
        return ApiResponse.ok(Map.of(
                "baselineId", e.getId(),
                "name", e.getName(),
                "content", e.getContent(),
                "format", e.getFormat(),
                "version", e.getVersion(),
                "createdAt", e.getCreatedAt()));
    }
}
