package com._ithon.speeksee.global.infra.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SpeekseeException extends RuntimeException {

    private final HttpStatus status;

    public SpeekseeException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
