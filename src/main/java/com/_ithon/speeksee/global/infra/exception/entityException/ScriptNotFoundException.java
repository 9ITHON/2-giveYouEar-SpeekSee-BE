package com._ithon.speeksee.global.infra.exception.entityException;

import org.springframework.http.HttpStatus;

import com._ithon.speeksee.global.infra.exception.SpeekseeException;
import com._ithon.speeksee.global.infra.exception.code.ErrorCode;

public class ScriptNotFoundException extends SpeekseeException {
	public ScriptNotFoundException() {
		super(HttpStatus.NOT_FOUND, ErrorCode.SCRIPT_NOT_FOUND);
	}
}
