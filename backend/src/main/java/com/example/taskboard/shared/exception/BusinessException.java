package com.example.taskboard.shared.exception;

public class BusinessException extends RuntimeException {

    private final String code;
    private final ErrorType type;

    public BusinessException(String code, String message, ErrorType type) {
        super(message);
        this.code = code;
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public ErrorType getType() {
        return type;
    }
}
