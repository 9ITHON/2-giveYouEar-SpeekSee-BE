package com._ithon.speeksee.global.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "액세스 토큰 응답 DTO")
public class AccessTokenResponseDto {

	@Schema(description = "발급된 액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
	private String accessToken;

	@Schema(description = "토큰 만료 시간 (초)", example = "3600")
	private int expiresIn;
}
