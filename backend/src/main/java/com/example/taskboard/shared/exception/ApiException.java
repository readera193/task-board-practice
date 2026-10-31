package com.example.taskboard.shared.exception;

/** 業務例外：帶有錯誤分類（{@link ErrorKind}）與穩定的錯誤代碼，由 GlobalExceptionHandler 轉成 Problem Details。 */
public class ApiException extends RuntimeException {

    private final ErrorKind kind;
    private final String code;

    public ApiException(ErrorKind kind, String code, String message) {
        super(message);
        this.kind = kind;
        this.code = code;
    }

    public ErrorKind getKind() {
        return kind;
    }

    public String getCode() {
        return code;
    }
}
