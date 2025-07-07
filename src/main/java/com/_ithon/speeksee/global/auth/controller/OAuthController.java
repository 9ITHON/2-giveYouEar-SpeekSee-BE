package com._ithon.speeksee.global.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._ithon.speeksee.global.auth.dto.request.OAuthRequestDto;
import com._ithon.speeksee.global.auth.dto.response.LoginResponseDto;
import com._ithon.speeksee.global.auth.service.OAuth2LoginService;
import com._ithon.speeksee.global.infra.exception.auth.SpeekseeAuthException;
import com._ithon.speeksee.global.infra.exception.code.ErrorCode;
import com._ithon.speeksee.global.infra.exception.response.ApiRes;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Tag(name = "OAuth2 로그인", description = "OAuth2를 활용한 소셜 로그인 API")
@RestController
@RequestMapping("/api/auth/oauth")
@RequiredArgsConstructor
public class OAuthController {

	private final OAuth2LoginService oAuth2LoginService;

	@Operation(summary = "OAuth 소셜 로그인", description = "KAKAO, GOOGLE OAuth 로그인")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", description = "소셜 로그인 성공"
		)
	})
	@PostMapping("/login")
	public ResponseEntity<ApiRes<LoginResponseDto>> loginWithOAuth2(@RequestBody @Valid OAuthRequestDto request) {
		LoginResponseDto response = oAuth2LoginService.login(request.getCode(), request.getProvider());
		return ResponseEntity.ok(ApiRes.success(response, "소셜 로그인 성공"));
	}

}