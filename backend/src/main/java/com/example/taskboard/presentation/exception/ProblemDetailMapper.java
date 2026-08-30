package com.example.taskboard.presentation.exception;

import com.example.taskboard.shared.result.AppError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public final class ProblemDetailMapper {

    private ProblemDetailMapper() {
    }

    public static ProblemDetail from(AppError error) {

        HttpStatus status = switch (error.code()) {
            case "TASK_NOT_FOUND" ->
                    HttpStatus.NOT_FOUND;

            case "TASK_INVALID_TITLE" ->
                    HttpStatus.BAD_REQUEST;

            default ->
                    HttpStatus.BAD_REQUEST;
        };

        ProblemDetail problemDetail =
                ProblemDetail.forStatus(status);

        problemDetail.setTitle(status.getReasonPhrase());
        problemDetail.setDetail(error.message());
        problemDetail.setProperty("code", error.code());

        return problemDetail;
    }
}