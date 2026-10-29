package com.example.taskboard.presentation.exception;

/** 單一欄位的驗證失敗訊息；驗證失敗回應的 {@code errors} 為此型別的陣列。 */
public record FieldViolation(String field, String message) {}
