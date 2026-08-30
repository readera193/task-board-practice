package com.example.taskboard.presentation.response;

import com.example.taskboard.presentation.exception.ProblemDetailMapper;
import com.example.taskboard.shared.result.Result;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

public final class ResultResponse {

    private ResultResponse() {
    }

    public static <T> ResponseEntity<?> from(Result<T> result) {

        if (result.isFailure()) {
            ProblemDetail problemDetail =
                    ProblemDetailMapper.from(result.getError());

            return ResponseEntity
                    .status(problemDetail.getStatus())
                    .body(problemDetail);
        }

        return ResponseEntity.ok(result.getValue());
    }
}