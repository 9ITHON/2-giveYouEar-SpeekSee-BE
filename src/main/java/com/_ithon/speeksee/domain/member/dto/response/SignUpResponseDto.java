package com._ithon.speeksee.domain.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "회원가입 응답 DTO")
public class SignUpResponseDto {

	@Schema(description = "사용자 ID", example = "1")
	private final Long userId;

	@Schema(description = "사용자 이메일", example = "user@example.com")
	private final String email;

	@Schema(description = "사용자 이름 또는 닉네임", example = "홍길동")
	private final String username;
}
