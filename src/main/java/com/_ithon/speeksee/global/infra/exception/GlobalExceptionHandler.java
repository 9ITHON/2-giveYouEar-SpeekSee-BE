package com._ithon.speeksee.global.infra.exception;

import com._ithon.speeksee.global.infra.exception.auth.SpeekseeAuthException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SpeekseeException.class)
    public ResponseEntity<ErrorResponse> handleSpeekseeException(SpeekseeException e) {
        log.error("SpeekseeException caught: {}", e.getMessage(), e);
        return buildResponse(e.getStatus(), e.getMessage(), getCauseMessage(e));
    }

    @ExceptionHandler(SpeekseeAuthException.class)
    public ResponseEntity<ErrorResponse> handleSpeekseeAuthException(SpeekseeAuthException e) {
        log.error("SpeekseeAuthException: ", e);
        return buildResponse(HttpStatus.UNAUTHORIZED, "인증관련 에러가 발생했습니다.", e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException: ", e);
        return buildResponse(HttpStatus.BAD_REQUEST, "요청 인자값이 올바르지 않습니다.", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException: ", e);
        String detailMessage = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return buildResponse(HttpStatus.BAD_REQUEST, "요청 인자값이 유효하지 않습니다.", detailMessage);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        log.error("AccessDeniedException: ", e);
        return buildResponse(HttpStatus.FORBIDDEN, "권한이 올바르지 않습니다.", e.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException e) {
        log.error("EntityNotFoundException: ", e);
        return buildResponse(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다.", e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        log.error("Unexpected error: ", e);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 예상치 못한 오류가 발생했습니다.", e.getMessage());
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, String cause) {
        ErrorResponse errorResponse = ErrorResponse.of(status.value(), message, cause);
        return ResponseEntity.status(status).body(errorResponse);
    }

    private String getCauseMessage(Throwable e) {
        return e.getCause() != null ? e.getCause().getMessage() : null;
    }
}
