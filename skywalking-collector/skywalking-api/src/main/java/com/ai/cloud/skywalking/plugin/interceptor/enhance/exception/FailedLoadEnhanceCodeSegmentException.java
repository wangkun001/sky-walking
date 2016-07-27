package com.ai.cloud.skywalking.plugin.interceptor.enhance.exception;

public class FailedLoadEnhanceCodeSegmentException extends Exception {
    public FailedLoadEnhanceCodeSegmentException(String message, Throwable e) {
        super(message, e);
    }
}
