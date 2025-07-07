package com._ithon.speeksee.global.auth.service;

import java.util.Optional;

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

	/**
	 * 소셜 로그인 사용자 처리: 이메일 기준으로 기존 회원인지 판단하고,
	 * provider 불일치 시 예외, 없으면 신규 등록
	 */
	public Member findOrCreate(OAuth2UserInfo userInfo, AuthProvider provider) {
		String email = userInfo.getEmail();

		return memberRepository.findByEmail(email)
			.map(existing -> {
				validateProviderMatch(existing, provider);
				return existing;
			})
			.orElseGet(() -> registerNewUser(userInfo, provider));
	}

	private void validateProviderMatch(Member existing, AuthProvider provider) {
		if (!existing.getAuthProvider().equals(provider)) {
			throw new OAuth2AuthenticationException(
				String.format("해당 이메일은 이미 %s 계정으로 가입되어 있습니다.", existing.getAuthProvider().name()));
		}
	}

	private Member registerNewUser(OAuth2UserInfo userInfo, AuthProvider provider) {
		log.info("신규 사용자 등록 - email: {}, provider: {}", userInfo.getEmail(), provider);
		Member member = Member.builder()
			.email(userInfo.getEmail())
			.authProvider(provider)
			.providerId(userInfo.getId())
			.isInfoCompleted(false)
			.build();

		return memberRepository.save(member);
	}

	public Optional<Member> findByProviderIdAndProvider(String providerId, AuthProvider provider) {
		return memberRepository.findByProviderIdAndAuthProvider(providerId, provider);
	}
}
