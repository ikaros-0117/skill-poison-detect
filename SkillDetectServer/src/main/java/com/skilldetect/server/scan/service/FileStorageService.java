package com.skilldetect.server.scan.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.skilldetect.server.common.BusinessException;
import com.skilldetect.server.config.ScanProperties;

@Service
public class FileStorageService {

    private static final char[] HEX = "0123456789abcdef".toCharArray();

    private final ScanProperties properties;

    public FileStorageService(ScanProperties properties) {
        this.properties = properties;
    }

    /** Save an uploaded zip to <baseDir>/<taskNo>/input.zip and return the path. */
    public String saveUpload(String taskNo, byte[] bytes) {
        try {
            Path dir = Paths.get(properties.getStorage().getBaseDir(), taskNo);
            Files.createDirectories(dir);
            Path file = dir.resolve("input.zip");
            Files.write(file, bytes);
            return file.toString();
        } catch (IOException ex) {
            throw new BusinessException(50010, "保存上传文件失败: " + ex.getMessage());
        }
    }

    /** Write a rendered report and return the stored path. */
    public String writeReport(String taskNo, String content, String extension) {
        try {
            Path dir = Paths.get(properties.getStorage().getBaseDir(), taskNo);
            Files.createDirectories(dir);
            Path file = dir.resolve("report." + extension);
            Files.write(file, (content == null ? "" : content).getBytes(StandardCharsets.UTF_8));
            return file.toString();
        } catch (IOException ex) {
            throw new BusinessException(50011, "写入报告失败: " + ex.getMessage());
        }
    }

    public void deleteDir(String taskNo) {
        try {
            Path dir = Paths.get(properties.getStorage().getBaseDir(), taskNo);
            if (!Files.exists(dir)) {
                return;
            }
            try (Stream<Path> stream = Files.walk(dir)) {
                stream.sorted((a, b) -> b.getNameCount() - a.getNameCount()).forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException ignored) {
                        // best-effort cleanup
                    }
                });
            }
        } catch (IOException ignored) {
            // best-effort cleanup
        }
    }

    public String readReport(String path) {
        try {
            return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new BusinessException(40403, "报告不存在或不可读");
        }
    }

    public static String sha256(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return toHex(digest.digest(bytes));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 unavailable", ex);
        }
    }

    private static String toHex(byte[] bytes) {
        char[] chars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            chars[i * 2] = HEX[v >>> 4];
            chars[i * 2 + 1] = HEX[v & 0x0F];
        }
        return new String(chars);
    }
}
