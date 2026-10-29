package com.example.taskboard.presentation.exception;

import com.example.taskboard.shared.exception.BusinessException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusinessException(BusinessException exception) {
        HttpStatus status =
                switch (exception.getType()) {
                    case VALIDATION -> HttpStatus.BAD_REQUEST;
                    case NOT_FOUND -> HttpStatus.NOT_FOUND;
                    case CONFLICT -> HttpStatus.CONFLICT;
                };

        ProblemDetail problemDetail = ProblemDetail.forStatus(status);

        problemDetail.setTitle(status.getReasonPhrase());
        problemDetail.setDetail(exception.getMessage());
        problemDetail.setProperty("code", exception.getCode());

        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception exception) {
        log.error("Unhandled exception", exception);

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);

        problemDetail.setTitle("Internal Server Error");
        problemDetail.setDetail("系統發生非預期錯誤");

        return problemDetail;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problemDetail.setTitle("Validation Failed");
        problemDetail.setDetail("請求資料驗證失敗");
        problemDetail.setProperty("code", "VALIDATION_ERROR");

        // 與 195Home 一致：errors 為 [{field, message}] 陣列，前端可直接逐筆顯示
        List<FieldViolation> errors =
                exception.getBindingResult().getFieldErrors().stream()
                        .map(
                                fieldError ->
                                        new FieldViolation(
                                                fieldError.getField(),
                                                fieldError.getDefaultMessage() != null
                                                        ? fieldError.getDefaultMessage()
                                                        : "欄位驗證失敗"))
                        .toList();

        problemDetail.setProperty("errors", errors);

        return createResponseEntity(problemDetail, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problemDetail.setTitle("Invalid Request Body");
        problemDetail.setDetail("請求內容格式不正確");
        problemDetail.setProperty("code", "INVALID_REQUEST_BODY");

        return createResponseEntity(problemDetail, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problemDetail.setTitle("Invalid Parameter");
        String name =
                exception instanceof MethodArgumentTypeMismatchException mismatch
                        ? mismatch.getName()
                        : exception.getPropertyName();
        problemDetail.setDetail("參數 " + name + " 格式不正確");
        problemDetail.setProperty("code", "INVALID_PARAMETER");

        return createResponseEntity(problemDetail, headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthenticationException(AuthenticationException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);

        problemDetail.setTitle("Unauthorized");
        problemDetail.setDetail("帳號或密碼錯誤");
        problemDetail.setProperty("code", "INVALID_CREDENTIALS");

        return problemDetail;
    }

    /** 授權失敗交還給 Spring Security 的 AccessDeniedHandler（回 403），避免被通用 Exception handler 吞成 500。 */
    @ExceptionHandler(AccessDeniedException.class)
    public void rethrowAccessDenied(AccessDeniedException exception) {
        throw exception;
    }
}
