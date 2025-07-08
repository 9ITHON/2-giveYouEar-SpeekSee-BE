package com._ithon.speeksee.global.auth.service;

import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com._ithon.speeksee.domain.member.entity.AuthProvider;
import com._ithon.speeksee.global.auth.dto.response.KakaoUserInfo;
import com._ithon.speeksee.global.auth.dto.response.OAuth2UserInfo;
import com._ithon.speeksee.global.infra.exception.auth.SpeekseeAuthException;

import org.springframework.beans.factory.annotation.Value;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoOAuth2Client implements OAuth2Client {

	@Value("${oauth.kakao.client-id}")
	private String clientId;

	@Value("${oauth.kakao.redirect-uri}")
	private String redirectUri;

	@Value("${oauth.kakao.client-secret}")
	private String clientSecret;

	protected final RestTemplate restTemplate = new RestTemplate();

	@Override
	public String getAccessToken(String code) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("client_id", clientId);
		params.add("redirect_uri", redirectUri);
		params.add("client_secret", clientSecret);
		params.add("code", code);
		params.add("grant_type", "authorization_code");

		log.info("OAuth Token Request Parameters:");
		log.info("code = {}", code);
		log.info("client_id = {}", clientId);
		log.info("redirect_uri = {}", redirectUri);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

		ResponseEntity<Map> response = restTemplate.postForEntity(
			"https://kauth.kakao.com/oauth/token", request, Map.class
		);

		if (response.getStatusCode() == HttpStatus.OK) {
			return (String)response.getBody().get("access_token");
		} else {
			throw new SpeekseeAuthException(HttpStatus.BAD_REQUEST, "Google OAuth2 토큰 요청 실패: " + response);
		}

	}

	// 받은 accessToken으로 사용자 정보 요청
	@Override
	public OAuth2UserInfo getUserInfo(String accessToken) {
		String userInfoUrl = "https://kapi.kakao.com/v2/user/me";
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);

		ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
			userInfoUrl,
			HttpMethod.GET,
			new HttpEntity<>(headers),
			new ParameterizedTypeReference<Map<String, Object>>() {
			}
		);

		if (response.getStatusCode() != HttpStatus.OK) {
			throw new SpeekseeAuthException(HttpStatus.UNAUTHORIZED, "카카오 사용자 정보 요청 실패");
		}

		log.info("response code = {}", response.getBody());

		Map<String, Object> attributes = response.getBody();
		return OAuth2UserInfo.of(AuthProvider.KAKAO, attributes);
	}

	@Override
	public AuthProvider getProvider() {
		return AuthProvider.KAKAO;
	}
}
