package com._ithon.speeksee.domain.voicefeedback.streaming.infra.session;

import com._ithon.speeksee.domain.voicefeedback.practice.entity.PracticeMode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticatedSession {
	private Long memberId;
	private String email;
	private Long scriptId;
	private PracticeMode mode;
}
