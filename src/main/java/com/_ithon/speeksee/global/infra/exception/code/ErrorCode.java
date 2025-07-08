package com._ithon.speeksee.global.infra.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	SUCCESS(0, "성공"),
	INVALID_ARGUMENT(1001, "요청 인자값이 유효하지 않습니다."),
	AUTH_FAIL(1002, "인증 실패"),
	NOT_FOUND(1003, "리소스를 찾을 수 없습니다."),
	FORBIDDEN(1004, "권한이 올바르지 않습니다."),
	INTERNAL_ERROR(1000, "서버 내부 오류"),
	OAUTH_AUTH_FAILED(1005, "OAuth 인증에 실패했습니다."),

	// --- 도메인 기반 상세 오류 ---
	MEMBER_NOT_FOUND(2001, "해당 사용자를 찾을 수 없습니다."),
	SCRIPT_GENERATION_FAILED(2002, "대본 생성에 실패했습니다."),
	SCRIPT_NOT_FOUND(2003, "해당 대본을 찾을 수 없습니다."),
	VOICE_FEEDBACK_INVALID(2004, "음성 피드백 요청이 유효하지 않습니다."), PraCTICE_NOT_FOUND(2005, "해당 연습을 찾을 수 없습니다.");

	private final int code;
	private final String message;
}
