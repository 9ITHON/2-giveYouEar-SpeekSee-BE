package com._ithon.speeksee.global.infra.exception;

import org.springframework.http.HttpStatus;

import com._ithon.speeksee.global.infra.exception.code.ErrorCode;

import lombok.Getter;

@Getter
public class SpeekseeException extends RuntimeException {

    private final HttpStatus status;
    private final ErrorCode errorCode;

    // 기존 메시지 기반 생성자
    public SpeekseeException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.errorCode = ErrorCode.INTERNAL_ERROR; // 기본값 설정 (혹은 null 허용)
    }

    // 새로 추가: ErrorCode 기반 생성자
    public SpeekseeException(HttpStatus status, ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.status = status;
        this.errorCode = errorCode;
    }
}
