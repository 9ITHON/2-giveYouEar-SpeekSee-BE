package com._ithon.speeksee.global.auth.dto.request;

import com._ithon.speeksee.domain.member.entity.AuthProvider;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "소셜 로그인 요청 DTO")
public class OAuthRequestDto {

	@Schema(description = "소셜 인가 코드", example = "abc123code...")
	private String code;

	@Schema(description = "소셜 로그인 공급자", example = "KAKAO")
	private AuthProvider provider;
}
