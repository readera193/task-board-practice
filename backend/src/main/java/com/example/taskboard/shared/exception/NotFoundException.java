package com.example.taskboard.shared.exception;

public class NotFoundException extends ApiException {

    public NotFoundException(String code, String message) {
        super(ErrorKind.NOT_FOUND, code, message);
    }
}
