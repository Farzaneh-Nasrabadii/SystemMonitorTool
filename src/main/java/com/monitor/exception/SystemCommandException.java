package com.monitor.exception;

public class SystemCommandException extends RuntimeException {
    public SystemCommandException(String message, Throwable cause) {
        super(message, cause);
    }
}