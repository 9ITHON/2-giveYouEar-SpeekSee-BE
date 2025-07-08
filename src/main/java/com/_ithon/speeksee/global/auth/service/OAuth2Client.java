package com._ithon.speeksee.global.auth.service;

import com._ithon.speeksee.domain.member.entity.AuthProvider;
import com._ithon.speeksee.global.auth.dto.response.OAuth2UserInfo;

public interface OAuth2Client {
	String getAccessToken(String code);

	OAuth2UserInfo getUserInfo(String accessToken);

	AuthProvider getProvider();
}
