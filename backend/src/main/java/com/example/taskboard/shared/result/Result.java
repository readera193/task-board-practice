package com.example.taskboard.shared.result;

public class Result<T> {

    private final T value;
    private final AppError error;
    private final boolean success;

    private Result(T value, AppError error, boolean success) {
        this.value = value;
        this.error = error;
        this.success = success;
    }

    public static <T> Result<T> success(T value) {
        return new Result<>(value, null, true);
    }

    public static Result<Void> success() {
        return new Result<>(null, null, true);
    }

    public static <T> Result<T> failure(AppError error) {
        return new Result<>(null, error, false);
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isFailure() {
        return !success;
    }

    public T getValue() {
        return value;
    }

    public AppError getError() {
        return error;
    }
}