package com._ithon.speeksee.global.auth.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
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

import com._ithon.speeksee.global.infra.exception.auth.SpeekseeAuthException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class GoogleOAuth2Client {

	@Value("${oauth.google.client-id}")
	private String clientId;

	@Value("${oauth.google.client-secret}")
	private String clientSecret;

	@Value("${oauth.google.redirect-uri}")
	private String redirectUri;

	private final RestTemplate restTemplate = new RestTemplate(); //외부 API 서버와 데이터를 주고받을 때 주로 사용

	public String getAccessToken(String code){
		String tokenUrl = "https://oauth2.googleapis.com/token";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // 구글이 이 형식을 원함

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("code", code);
		params.add("client_id", clientId);
		params.add("client_secret", clientSecret);
		params.add("redirect_uri", redirectUri);
		params.add("grant_type", "authorization_code");

		// // 로그 출력
		// log.info("OAuth Token Request Parameters:");
		// log.info("code = {}", code);
		// log.info("client_id = {}", clientId);
		// log.info("redirect_uri = {}", redirectUri);
		// log.info("grant_type = authorization_code");

		HttpEntity<?> request = new HttpEntity<>(params, headers);

		// 서버에 post 요청을 보냄
		ResponseEntity<Map> response = restTemplate.postForEntity(
			"https://oauth2.googleapis.com/token", request, Map.class);


		// 구글로부터 받은 access token
		if (response.getStatusCode() == HttpStatus.OK) {
			return (String) response.getBody().get("access_token");
		} else {
			throw new SpeekseeAuthException(HttpStatus.BAD_REQUEST, "Google OAuth2 토큰 요청 실패: " + response);
		}
	}

	// 받은 accessToken으로 사용자 정보 요청
	public Map<String, Object> getUserInfo(String accessToken) {
		String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);

		HttpEntity<?> entity = new HttpEntity<>(headers);

		ResponseEntity<Map> response = restTemplate.exchange(
			userInfoUrl,
			HttpMethod.GET,
			entity,
			Map.class
		);

		if (response.getStatusCode() != HttpStatus.OK) {
			throw new RuntimeException("Google 사용자 정보 요청 실패: " + response);
		}

		return response.getBody();
	}
}
