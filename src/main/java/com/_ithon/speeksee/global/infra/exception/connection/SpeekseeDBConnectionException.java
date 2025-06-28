package com._ithon.speeksee.global.infra.exception.connection;

import com._ithon.speeksee.global.infra.exception.SpeekseeException;
import org.springframework.http.HttpStatus;

public class SpeekseeDBConnectionException extends SpeekseeException {

    private SpeekseeDBConnectionException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "DB Connection 과정 중 문제가 발생했습니다.");
    }
}
