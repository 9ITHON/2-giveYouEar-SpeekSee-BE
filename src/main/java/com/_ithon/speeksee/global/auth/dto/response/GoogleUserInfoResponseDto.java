package com._ithon.speeksee.global.auth.dto.response;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GoogleUserInfoResponseDto implements OAuth2UserInfo {

	private Map<String, Object> attributes;

	@Override
	public final String getEmail() {
		return (String)attributes.get("email");
	}

	@Override
	public final String getName() {
		return (String)attributes.get("name");
	}

	@Override
	public final Map<String, Object> getAttributes() {
		return attributes;
	}
}
