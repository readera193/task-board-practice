package com.example.taskboard.shared.exception;

public class NotFoundException extends BusinessException {

    public NotFoundException(String code, String message) {
        super(code, message, ErrorType.NOT_FOUND);
    }
}
