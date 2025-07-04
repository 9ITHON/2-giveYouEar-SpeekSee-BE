package com._ithon.speeksee.global.auth.oauth.dto.reponse;

import java.util.Map;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GoogleUserInfoResponse implements OAuth2UserInfo{

	@Override
	public String getEmail() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return null;
	}
}
