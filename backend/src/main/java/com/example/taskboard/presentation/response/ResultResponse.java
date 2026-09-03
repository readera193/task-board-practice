package com.example.taskboard.presentation.response;

import com.example.taskboard.presentation.exception.ProblemDetailMapper;
import com.example.taskboard.shared.result.Result;
import java.util.function.Function;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

public final class ResultResponse {

    private ResultResponse() {}

    public static <T, R> ResponseEntity<?> from(Result<T> result, Function<T, R> mapper) {

        if (result.isFailure()) {
            ProblemDetail problemDetail = ProblemDetailMapper.from(result.getError());

            return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
        }

        return ResponseEntity.ok(mapper.apply(result.getValue()));
    }

    public static ResponseEntity<?> noContent(Result<Void> result) {

        if (result.isFailure()) {
            ProblemDetail problemDetail = ProblemDetailMapper.from(result.getError());

            return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
        }

        return ResponseEntity.noContent().build();
    }
}
