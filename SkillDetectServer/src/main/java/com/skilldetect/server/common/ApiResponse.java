package com.skilldetect.server.common;

import java.util.UUID;

/** Unified HTTP response envelope: {code, message, requestId, data}. */
public record ApiResponse<T>(int code, String message, String requestId, T data) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(0, "ok", newRequestId(), data);
    }

    public static ApiResponse<Void> error(int code, String message) {
        return new ApiResponse<>(code, message, newRequestId(), null);
    }

    private static String newRequestId() {
        return "req_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}
