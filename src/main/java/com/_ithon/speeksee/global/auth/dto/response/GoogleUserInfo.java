package com._ithon.speeksee.global.auth.dto.response;

import java.util.Map;

import com._ithon.speeksee.domain.member.entity.AuthProvider;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GoogleUserInfo implements OAuth2UserInfo {

	private Map<String, Object> attributes;

	@Override
	public final String getEmail() {
		return (String)attributes.get("email");
	}

	@Override
	public String getId() {
		return (String)attributes.get("id");
	}

	@Override
	public AuthProvider getProvider() {
		return AuthProvider.GOOGLE;
	}
}
