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

	@Schema(description = "추가정보(닉네임, 생년월일)을 입력이 필요한지 나타내는 flag", example = "true")
	private final boolean needsAdditionalInfo;

	public static LoginResponseDto from(String accessToken, String refreshToken, long expiresIn,
		MemberInfoResponseDto memberInfo) {
		return LoginResponseDto.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.expiresIn(expiresIn)
			.memberInfo(memberInfo)
			.needsAdditionalInfo(false)
			.build();
	}

	// 추가 정보 필요 응답
	public static LoginResponseDto needsAdditionalInfo(String accessToken, String refreshToken, long expiresIn,
		MemberInfoResponseDto member) {
		return new LoginResponseDto(accessToken, refreshToken, expiresIn, member, true);
	}
}