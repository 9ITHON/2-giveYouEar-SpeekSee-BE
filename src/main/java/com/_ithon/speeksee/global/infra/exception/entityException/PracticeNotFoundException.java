package com._ithon.speeksee.global.infra.exception.entityException;

import org.springframework.http.HttpStatus;

import com._ithon.speeksee.global.infra.exception.SpeekseeException;
import com._ithon.speeksee.global.infra.exception.code.ErrorCode;

public class PracticeNotFoundException extends SpeekseeException{
	public PracticeNotFoundException() {
		super(HttpStatus.NOT_FOUND, ErrorCode.PraCTICE_NOT_FOUND);
	}
}
