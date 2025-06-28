package com._ithon.speeksee.global.infra.exception;

import java.time.LocalDateTime;

public record ErrorResponse (
        int status,
        String message,
        String cause,
        LocalDateTime time
) {
    public static ErrorResponse of(SpeekseeException exception) {
        return new ErrorResponse(
                exception.getStatus().value(),
                exception.getMessage(),
                exception.getCause() != null ? exception.getCause().getMessage() : null,

                LocalDateTime.now()
        );
    }

    public static ErrorResponse of(int status, String message, String cause) {
        return new ErrorResponse(status, message, cause, LocalDateTime.now());
    }
}
