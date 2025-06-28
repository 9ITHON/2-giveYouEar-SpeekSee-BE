package com._ithon.speeksee.global.infra.exception.auth;

import com._ithon.speeksee.global.infra.exception.SpeekseeException;
import org.springframework.http.HttpStatus;

public class SpeekseeAuthException extends SpeekseeException {
    public SpeekseeAuthException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
