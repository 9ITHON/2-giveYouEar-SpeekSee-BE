package com._ithon.speeksee.global.auth.dto.response;

import java.util.Map;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GoogleUserInfoResponseDto implements OAuth2UserInfo{

	private Map<String, Object> attributes;

	@Override
	public String getEmail() {
		return (String) attributes.get("email");
	}

	@Override
	public String getName() {
		return (String) attributes.get("name");
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}
}
