package com._ithon.speeksee.global.infra.exception.entityException;

import org.springframework.http.HttpStatus;

import com._ithon.speeksee.global.infra.exception.SpeekseeException;
import com._ithon.speeksee.global.infra.exception.code.ErrorCode;

public class MemberNotFoundException extends SpeekseeException {
	public MemberNotFoundException() {
		super(HttpStatus.NOT_FOUND, ErrorCode.MEMBER_NOT_FOUND);
	}
}