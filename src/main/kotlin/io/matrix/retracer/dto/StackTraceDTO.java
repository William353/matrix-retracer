package io.matrix.retracer.dto;

public class StackTraceDTO {
    private String stackKey;
    private String stackTrace;

    public StackTraceDTO(String stackKey, String stackTrace) {
        this.stackKey = stackKey;
        this.stackTrace = stackTrace;
    }

    public String getStackKey() {
        return stackKey;
    }

    public String getStackTrace() {
        return stackTrace;
    }
}
