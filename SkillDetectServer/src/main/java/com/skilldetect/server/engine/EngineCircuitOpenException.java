package com.skilldetect.server.engine;

/** Raised when the engine circuit breaker is open and a scan request is refused. */
public class EngineCircuitOpenException extends RuntimeException {

    public EngineCircuitOpenException(String message) {
        super(message);
    }
}
