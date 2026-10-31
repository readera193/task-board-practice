package com.example.taskboard.shared.exception;

/** 業務錯誤分類：與傳輸層（HTTP）無關，由 GlobalExceptionHandler 對應成狀態碼。 */
public enum ErrorKind {
    BAD_REQUEST,
    UNAUTHORIZED,
    FORBIDDEN,
    NOT_FOUND,
    CONFLICT,
    GONE,
    TOO_MANY_REQUESTS,
    INTERNAL
}
