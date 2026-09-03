package com.skilldetect.server.common;

import java.util.UUID;

/** Unified HTTP response envelope: {code, message, requestId, data}. */
public final class ApiResponse<T> {

    private final int code;
    private final String message;
    private final String requestId;
    private final T data;

    public ApiResponse(int code, String message, String requestId, T data) {
        this.code = code;
        this.message = message;
        this.requestId = requestId;
        this.data = data;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(0, "ok", newRequestId(), data);
    }

    public static ApiResponse<Void> error(int code, String message) {
        return new ApiResponse<>(code, message, newRequestId(), null);
    }

    private static String newRequestId() {
        return "req_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    public int getCode() { return code; }
    public String getMessage() { return message; }
    public String getRequestId() { return requestId; }
    public T getData() { return data; }
}
