package com._ithon.speeksee.global.auth.dto.response;

import com._ithon.speeksee.domain.member.dto.response.MemberInfoResponseDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "로그인 응답 DTO")
public class LoginResponseDto {

	@Schema(description = "발급된 액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
	private final String accessToken;

	@Schema(description = "발급된 리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
	private final String refreshToken;

	@Schema(description = "액세스 토큰 만료 시간(초)", example = "3600")
	private final long expiresIn;

	@Schema(description = "로그인한 사용자 정보")
	private final MemberInfoResponseDto memberInfo;

	public static LoginResponseDto from(String accessToken, String refreshToken, long expiresIn,
		MemberInfoResponseDto memberInfo) {
		return LoginResponseDto.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.expiresIn(expiresIn)
			.memberInfo(memberInfo)
			.build();
	}
}