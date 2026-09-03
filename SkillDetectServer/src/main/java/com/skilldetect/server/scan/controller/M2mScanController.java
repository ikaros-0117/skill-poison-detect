package com.skilldetect.server.scan.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skilldetect.server.common.ApiResponse;
import com.skilldetect.server.common.BusinessException;
import com.skilldetect.server.scan.domain.ScanStatus;
import com.skilldetect.server.scan.domain.ScanTaskEntity;
import com.skilldetect.server.scan.service.FileStorageService;
import com.skilldetect.server.scan.service.ScanService;

@RestController
@RequestMapping("/api/m2m/v1")
public class M2mScanController {

    private final ScanService scanService;
    private final FileStorageService fileStorage;
    private final ObjectMapper objectMapper;

    public M2mScanController(ScanService scanService,
                             FileStorageService fileStorage,
                             ObjectMapper objectMapper) {
        this.scanService = scanService;
        this.fileStorage = fileStorage;
        this.objectMapper = objectMapper;
    }

    @PostMapping(value = "/scans", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ScanDtos.Create>> createScan(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "useLlm", defaultValue = "false") boolean useLlm,
            @RequestParam(value = "riskThreshold", required = false) Integer riskThreshold,
            @RequestParam(value = "metadata", required = false) String metadataJson) {
        byte[] bytes = readBytes(file);
        Map<String, Object> metadata = parseMetadata(metadataJson);
        String taskId = scanService.create(bytes, useLlm, riskThreshold, "sarif", null, metadata);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ApiResponse.ok(new ScanDtos.Create(taskId)));
    }

    @GetMapping("/scans/{taskId}")
    public ApiResponse<ScanDtos.Task> getTask(@PathVariable String taskId) {
        return ApiResponse.ok(scanService.toDto(scanService.requireTask(taskId)));
    }

    @GetMapping(value = "/scans/{taskId}/report/sarif", produces = "application/sarif+json")
    public ResponseEntity<String> getSarif(@PathVariable String taskId) {
        ScanTaskEntity task = scanService.requireTask(taskId);
        if (!ScanStatus.SUCCEEDED.name().equals(task.getStatus()) || task.getReportPath() == null) {
            throw new BusinessException(HttpStatus.CONFLICT, 40901, "报告尚未生成");
        }
        String report = fileStorage.readReport(task.getReportPath());
        return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/sarif+json")).body(report);
    }

    @PostMapping("/scans/{taskId}/retry")
    public ResponseEntity<ApiResponse<Map<String, String>>> retry(@PathVariable String taskId) {
        scanService.retry(taskId);
        Map<String, String> body = new LinkedHashMap<>();
        body.put("taskId", taskId);
        body.put("status", "QUEUED");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ApiResponse.ok(body));
    }

    @PostMapping("/scans/{taskId}/cancel")
    public ResponseEntity<ApiResponse<Map<String, String>>> cancel(@PathVariable String taskId) {
        scanService.cancel(taskId);
        Map<String, String> body = new LinkedHashMap<>();
        body.put("taskId", taskId);
        body.put("status", "CANCELED");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ApiResponse.ok(body));
    }

    private byte[] readBytes(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(40003, "上传文件为空");
        }
        try {
            return file.getBytes();
        } catch (IOException ex) {
            throw new BusinessException(50010, "读取上传文件失败: " + ex.getMessage());
        }
    }

    private Map<String, Object> parseMetadata(String metadataJson) {
        if (metadataJson == null || metadataJson.trim().isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(metadataJson, new TypeReference<Map<String, Object>>() {});
        } catch (IOException ex) {
            throw new BusinessException(40004, "metadata 不是合法 JSON: " + ex.getMessage());
        }
    }
}
