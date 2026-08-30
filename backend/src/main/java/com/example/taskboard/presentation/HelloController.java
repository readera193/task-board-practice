package com.example.taskboard.presentation;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.taskboard.application.task.TaskErrors;
import com.example.taskboard.presentation.exception.ProblemDetailMapper;
import com.example.taskboard.shared.exception.BusinessException;
import com.example.taskboard.shared.result.Result;

@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello Spring Boot DevTools!";
    }

    @GetMapping("/error")
    public String error() {
        throw new RuntimeException("Test exception");
    }

    @GetMapping("/business-error")
    public String businessError() {
        throw new BusinessException("系統資料進入不一致狀態");
    }

    @GetMapping("/result-error")
    public ResponseEntity<?> resultError() {

        Result<String> result = Result.failure(TaskErrors.NOT_FOUND);

        if (result.isFailure()) {
            ProblemDetail problemDetail = ProblemDetailMapper.from(result.getError());

            return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
        }

        return ResponseEntity.ok(result.getValue());
    }
}
