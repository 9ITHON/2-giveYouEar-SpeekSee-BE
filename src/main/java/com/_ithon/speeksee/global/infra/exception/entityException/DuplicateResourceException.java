package com._ithon.speeksee.global.infra.exception.entityException;

import org.springframework.http.HttpStatus;

import com._ithon.speeksee.global.infra.exception.SpeekseeException;

public class DuplicateResourceException extends SpeekseeException {
	public DuplicateResourceException(String resourceName, String value) {
		super(HttpStatus.CONFLICT, String.format("중복된 %s입니다: %s", resourceName, value));
	}

	public DuplicateResourceException(String message) {
		super(HttpStatus.CONFLICT, message);
	}
}
