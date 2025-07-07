package com._ithon.speeksee.global.auth.service;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;

import com._ithon.speeksee.domain.member.entity.AuthProvider;
import com._ithon.speeksee.domain.member.entity.Member;
import com._ithon.speeksee.domain.member.repository.MemberRepository;
import com._ithon.speeksee.global.auth.dto.response.OAuth2UserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthService {

	private final MemberRepository memberRepository;

	public Member findOrCreate(OAuth2UserInfo userInfo, AuthProvider provider) {
		String email = userInfo.getEmail();

		return memberRepository.findByEmail(email)
			.map(existing -> {
				// 이미 존재하는데 provider가 다르면 예외 처리
				if (!existing.getAuthProvider().equals(provider)) {
					throw new OAuth2AuthenticationException("다른 소셜 계정으로 가입된 이메일입니다.");
				}
				return existing;
			})
			.orElseGet(() -> registerNewUser(userInfo, provider));
	}

	private Member registerNewUser(OAuth2UserInfo userInfo, AuthProvider provider) {
		log.info("registerNewUser");
		log.info("userInfo: {}", userInfo.getEmail());
		Member member = Member.builder()
			.email(userInfo.getEmail())
			.authProvider(provider)
			.build();

		return memberRepository.save(member);
	}
}
