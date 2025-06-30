package com._ithon.speeksee.domain.member.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com._ithon.speeksee.domain.member.dto.request.SignUpRequestDto;
import com._ithon.speeksee.domain.member.entity.Member;
import com._ithon.speeksee.domain.member.repository.MemberRepository;
import com._ithon.speeksee.global.infra.exception.auth.SpeekseeAuthException;

@Service
public class MemberService {
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
		this.memberRepository = memberRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public Member signUp(SignUpRequestDto signUpRequestDto) {
		if (memberRepository.existsByEmail(signUpRequestDto.getEmail())) {
			throw new SpeekseeAuthException(HttpStatus.CONFLICT, "이메일이 이미 존재합니다");
		}

		String encodedPassword = passwordEncoder.encode(signUpRequestDto.getPassword());

		Member member = Member.builder()
			.email(signUpRequestDto.getEmail())
			.passwordHash(encodedPassword)
			.username(signUpRequestDto.getUsername())
			.build();

		memberRepository.save(member);
		return member;
	}
}
