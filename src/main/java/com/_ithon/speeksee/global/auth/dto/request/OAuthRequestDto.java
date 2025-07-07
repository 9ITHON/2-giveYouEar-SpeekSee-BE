package com._ithon.speeksee.global.auth.dto.request;

import com._ithon.speeksee.domain.member.entity.AuthProvider;

import lombok.Getter;

@Getter
public class OAuthRequestDto {
	private String code;
	private AuthProvider provider;
}
