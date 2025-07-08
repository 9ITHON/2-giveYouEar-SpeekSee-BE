package com._ithon.speeksee.global.auth.dto.response;

import java.util.Map;

import com._ithon.speeksee.domain.member.entity.AuthProvider;

public interface OAuth2UserInfo {

	String getEmail();

	String getId();

	AuthProvider getProvider();

	static OAuth2UserInfo of(AuthProvider authProvider, Map<String, Object> attributes) {
		return switch (authProvider) {
			case KAKAO -> new KakaoUserInfo(attributes);
			case GOOGLE -> new GoogleUserInfo(attributes);
			default -> throw new IllegalArgumentException("지원하지 않는 OAuth 공급자입니다: " + authProvider);
		};
	}
}
