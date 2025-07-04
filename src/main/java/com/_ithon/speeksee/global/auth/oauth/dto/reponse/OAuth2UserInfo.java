package com._ithon.speeksee.global.auth.oauth.dto.reponse;

import java.util.Map;

public interface OAuth2UserInfo {
	String getEmail();
	String getName();
	Map<String, Object> getAttributes();
}
