package com._ithon.speeksee.domain.voicefeedback.streaming.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticatedSession {
	private Long memberId;
	private String email;
	private Long scriptId;
}
