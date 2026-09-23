package com.example.taskboard.shared.exception;

public class ConflictException extends BusinessException {

    public ConflictException(String code, String message) {
        super(code, message, ErrorType.CONFLICT);
    }
}
