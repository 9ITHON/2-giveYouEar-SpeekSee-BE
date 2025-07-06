package com._ithon.speeksee.global.infra.exception;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com._ithon.speeksee.global.infra.exception.code.ErrorCode;
import com._ithon.speeksee.global.infra.exception.entityException.MemberNotFoundException;
import com._ithon.speeksee.global.infra.exception.entityException.PracticeNotFoundException;
import com._ithon.speeksee.global.infra.exception.entityException.ScriptNotFoundException;
import com._ithon.speeksee.global.infra.exception.response.ApiRes;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(SpeekseeException.class)
	public ResponseEntity<ApiRes<Void>> handleSpeekseeException(SpeekseeException e) {
		log.error("SpeekseeException caught: {}", e.getMessage(), e);
		return ResponseEntity.status(e.getStatus())
			.body(ApiRes.failure(e.getStatus(), e.getMessage(), ErrorCode.INTERNAL_ERROR));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiRes<Void>> handleValidationException(MethodArgumentNotValidException e) {
		String detailMessage = e.getBindingResult().getFieldErrors().stream()
			.map(err -> err.getField() + ": " + err.getDefaultMessage())
			.collect(Collectors.joining(", "));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(ApiRes.failure(HttpStatus.BAD_REQUEST, detailMessage, ErrorCode.INVALID_ARGUMENT));
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ApiRes<Void>> handleEntityNotFound(EntityNotFoundException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(ApiRes.failure(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND));
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiRes<Void>> handleAccessDeniedException(AccessDeniedException e) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
			.body(ApiRes.failure(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN));
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ApiRes<Void>> handleRuntimeException(RuntimeException e) {
		log.error("Unexpected error: ", e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(ApiRes.failure(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_ERROR));
	}

	@ExceptionHandler(ScriptNotFoundException.class)
	public ResponseEntity<ApiRes<Void>> handleScriptNotFound(ScriptNotFoundException e) {
		return ResponseEntity.status(e.getStatus())
			.body(ApiRes.failure(e.getStatus(), e.getMessage(), e.getErrorCode()));
	}

	@ExceptionHandler(MemberNotFoundException.class)
	public ResponseEntity<ApiRes<Void>> handleMemberNotFound(MemberNotFoundException e) {
		return ResponseEntity.status(e.getStatus())
			.body(ApiRes.failure(e.getStatus(), e.getMessage(), e.getErrorCode()));
	}

	@ExceptionHandler(PracticeNotFoundException.class)
	public ResponseEntity<ApiRes<Void>> handlePracticeNotFound(PracticeNotFoundException e) {
		return ResponseEntity.status(e.getStatus())
			.body(ApiRes.failure(e.getStatus(), e.getMessage(), e.getErrorCode()));
	}
}
