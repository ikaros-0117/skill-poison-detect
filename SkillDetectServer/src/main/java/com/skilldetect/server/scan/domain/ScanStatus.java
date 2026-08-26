package com.skilldetect.server.scan.domain;

public enum ScanStatus {
    PENDING,
    QUEUED,
    RUNNING,
    SUCCEEDED,
    FAILED,
    CANCELED
}
