package com._ithon.speeksee.global.infra.exception.auth;

import org.springframework.http.HttpStatus;

import com._ithon.speeksee.global.infra.exception.SpeekseeException;
import com._ithon.speeksee.global.infra.exception.code.ErrorCode;

public class OAuth2AuthenticationException extends SpeekseeException {

	public OAuth2AuthenticationException() {
		super(HttpStatus.UNAUTHORIZED, ErrorCode.OAUTH_AUTH_FAILED);
	}
}
