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

import com.skilldetect.server.common.ApiResponse;
import com.skilldetect.server.common.BusinessException;
import com.skilldetect.server.scan.domain.ScanStatus;
import com.skilldetect.server.scan.domain.ScanTaskEntity;
import com.skilldetect.server.scan.service.FileStorageService;
import com.skilldetect.server.scan.service.ScanService;

@RestController
@RequestMapping("/api/v1")
public class HmiScanController {

    private final ScanService scanService;
    private final FileStorageService fileStorage;

    public HmiScanController(ScanService scanService, FileStorageService fileStorage) {
        this.scanService = scanService;
        this.fileStorage = fileStorage;
    }

    @PostMapping(value = "/scans", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ScanDtos.Create>> createScan(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "useLlm", defaultValue = "false") boolean useLlm,
            @RequestParam(value = "riskThreshold", required = false) Integer riskThreshold,
            @RequestParam(value = "baselineId", required = false) Long baselineId) {
        byte[] bytes = readBytes(file);
        String taskId = scanService.create(bytes, useLlm, riskThreshold, "json", baselineId,
                Collections.<String, Object>emptyMap());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ApiResponse.ok(new ScanDtos.Create(taskId)));
    }

    @GetMapping("/scans/{taskId}")
    public ApiResponse<ScanDtos.Task> getTask(@PathVariable String taskId) {
        return ApiResponse.ok(scanService.toDto(scanService.requireTask(taskId)));
    }

    @GetMapping("/scans")
    public ApiResponse<ScanDtos.Page<ScanDtos.Task>> listTasks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(scanService.listTasks(page, size));
    }

    @GetMapping(value = "/scans/{taskId}/report", produces = {MediaType.APPLICATION_JSON_VALUE, "text/markdown"})
    public ResponseEntity<String> getReport(@PathVariable String taskId,
                                            @RequestParam(defaultValue = "json") String format) {
        ScanTaskEntity task = scanService.requireTask(taskId);
        if (!ScanStatus.SUCCEEDED.name().equals(task.getStatus()) || task.getReportPath() == null) {
            throw new BusinessException(HttpStatus.CONFLICT, 40901, "报告尚未生成");
        }
        if ("markdown".equalsIgnoreCase(format)) {
            return ResponseEntity.ok().contentType(MediaType.parseMediaType("text/markdown"))
                    .body(toMarkdown(task));
        }
        String report = fileStorage.readReport(task.getReportPath());
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(report);
    }

    @GetMapping("/scans/{taskId}/findings")
    public ApiResponse<ScanDtos.Page<ScanDtos.Finding>> listFindings(
            @PathVariable String taskId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String ruleId,
            @RequestParam(required = false) String category) {
        return ApiResponse.ok(scanService.listFindings(taskId, page, size, severity, ruleId, category));
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

    private String toMarkdown(ScanTaskEntity task) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Skill 扫描报告\n\n");
        sb.append("- taskId: ").append(task.getTaskNo()).append('\n');
        sb.append("- status: ").append(task.getStatus()).append('\n');
        sb.append("- riskScore: ").append(task.getRiskScore()).append('\n');
        sb.append("- severity: ").append(task.getSeverity()).append('\n');
        sb.append("- recommendation: ").append(task.getRecommendation()).append('\n');
        sb.append("- pass: ").append(task.getPass()).append('\n');
        return sb.toString();
    }
}
