package com._ithon.speeksee.global.auth.oauth.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com._ithon.speeksee.domain.member.entity.AuthProvider;
import com._ithon.speeksee.domain.member.entity.Member;
import com._ithon.speeksee.global.auth.dto.response.LoginResponseDto;
import com._ithon.speeksee.global.auth.oauth.dto.reponse.GoogleUserInfoResponseDto;
import com._ithon.speeksee.global.auth.oauth.dto.reponse.OAuth2UserInfo;
import com._ithon.speeksee.global.auth.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginService {

	private final GoogleOAuth2Client googleOAuth2Client;
	private final OAuthService oAuthService;
	private final AuthService authService;

	public LoginResponseDto loginWithGoogle(String code) {
		//  access_token
		String accessToken = googleOAuth2Client.getAccessToken(code);

		// access_token → 사용자 정보
		Map<String, Object> attributes = googleOAuth2Client.getUserInfo(accessToken);

		log.info("attrivure = {}", attributes);

		// userInfo 파싱
		OAuth2UserInfo userInfo = new GoogleUserInfoResponseDto(attributes);

		// 회원 조회 or 자동 가입
		Member member = oAuthService.findOrCreate(userInfo, AuthProvider.GOOGLE);

		// JWT 발급 + 로그인 응답 생성
		return authService.login(member);
	}
}
