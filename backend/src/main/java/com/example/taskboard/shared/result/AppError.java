package com.example.taskboard.shared.result;

public record AppError(
        String code,
        String message,
        ErrorType type
) {
}