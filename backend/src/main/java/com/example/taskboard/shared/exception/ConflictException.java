package com.example.taskboard.shared.exception;

public class ConflictException extends ApiException {

    public ConflictException(String code, String message) {
        super(ErrorKind.CONFLICT, code, message);
    }
}
