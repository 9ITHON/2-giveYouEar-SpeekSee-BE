package com._ithon.speeksee.global.auth.oauth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._ithon.speeksee.global.auth.dto.response.LoginResponseDto;
import com._ithon.speeksee.global.auth.oauth.dto.request.GoogleOAuthRequestDto;
import com._ithon.speeksee.global.auth.oauth.service.OAuth2LoginService;
import com._ithon.speeksee.global.infra.exception.auth.SpeekseeAuthException;
import com._ithon.speeksee.global.infra.exception.code.ErrorCode;
import com._ithon.speeksee.global.infra.exception.response.ApiRes;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth/oauth")
@RequiredArgsConstructor
public class OAuthController {

	private final OAuth2LoginService oAuth2LoginService;

	@PostMapping("/google")
	public ResponseEntity<ApiRes<LoginResponseDto>> googleLogin(@RequestBody GoogleOAuthRequestDto request) {
		try {
			LoginResponseDto response = oAuth2LoginService.loginWithGoogle(request.getCode());
			return ResponseEntity.ok(ApiRes.success(response, "Google 로그인 성공"));

		} catch (SpeekseeAuthException e) {

			return ResponseEntity
				.status(e.getStatus())
				.body(ApiRes.failure(e.getStatus(), e.getMessage(), ErrorCode.AUTH_FAIL));

		} catch (IllegalArgumentException | NullPointerException e) {

			return ResponseEntity
				.badRequest()
				.body(ApiRes.failure(HttpStatus.BAD_REQUEST, "유효하지 않은 인증 코드입니다.", ErrorCode.AUTH_FAIL));

		} catch (Exception e) {

			return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ApiRes.failure(HttpStatus.INTERNAL_SERVER_ERROR, "Google 로그인 처리 중 오류가 발생했습니다.", ErrorCode.INTERNAL_ERROR));
		}
	}
}
