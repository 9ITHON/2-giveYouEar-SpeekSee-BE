package com._ithon.speeksee.domain.member.service;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com._ithon.speeksee.domain.member.dto.request.SignUpRequestDto;
import com._ithon.speeksee.domain.member.dto.response.MemberInfoResponseDto;
import com._ithon.speeksee.domain.member.entity.Member;
import com._ithon.speeksee.domain.member.repository.MemberRepository;
import com._ithon.speeksee.global.infra.exception.auth.SpeekseeAuthException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	public MemberInfoResponseDto getMyInfo(Member member) {
		return MemberInfoResponseDto.builder()
			.userId(member.getId())
			.email(member.getEmail())
			.username(member.getUsername())
			.currentLevel(member.getCurrentLevel())
			.totalExp(member.getTotalExp())
			.consecutiveDays(member.getConsecutiveDays())
			.createdAt(member.getCreatedAt())
			.build();
	}

	public Member findByEmail(String email) {
		return memberRepository.findByEmail(email)
			.orElseThrow(() -> new NoSuchElementException("존재하지 않는 이메일입니다: " + email));
	}
}
