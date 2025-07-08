package com._ithon.speeksee.domain.member.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com._ithon.speeksee.domain.member.dto.request.SignUpRequestDto;
import com._ithon.speeksee.domain.member.entity.Member;
import com._ithon.speeksee.domain.member.repository.MemberRepository;
import com._ithon.speeksee.global.infra.exception.auth.SpeekseeAuthException;
import com._ithon.speeksee.global.infra.exception.entityException.DuplicateResourceException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberSignupService {
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	public Member signUp(SignUpRequestDto signUpRequestDto) {
		if (memberRepository.existsByEmail(signUpRequestDto.getEmail())) {
			throw new DuplicateResourceException("이메일", signUpRequestDto.getEmail());
		}

		if (memberRepository.existsByNickname(signUpRequestDto.getNickname())) {
			throw new DuplicateResourceException("닉네임", signUpRequestDto.getNickname());
		}

		String encodedPassword = passwordEncoder.encode(signUpRequestDto.getPassword());

		Member member = signUpRequestDto.toEntity(encodedPassword);

		memberRepository.save(member);
		return member;
	}
}
