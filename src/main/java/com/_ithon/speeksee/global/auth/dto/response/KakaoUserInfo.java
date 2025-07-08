package com._ithon.speeksee.global.auth.dto.response;

import java.util.Map;

import org.checkerframework.checker.units.qual.A;

import com._ithon.speeksee.domain.member.entity.AuthProvider;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KakaoUserInfo implements OAuth2UserInfo {

	private final Map<String, Object> attributes;

	@Override
	public String getEmail() { // 카카오는 kakao_account{email:..} 이렇게 주더라 -> 중첩구조라 꺼내써야됨
		Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");
		return kakaoAccount != null ? (String)kakaoAccount.get("email") : null;
	}

	@Override
	public String getId() { // 카카오는 id를 Long으로 줌
		Object id = attributes.get("id");
		return id != null ? String.valueOf(id) : null;
	}

	@Override
	public AuthProvider getProvider() {
		return AuthProvider.KAKAO;
	}
}
