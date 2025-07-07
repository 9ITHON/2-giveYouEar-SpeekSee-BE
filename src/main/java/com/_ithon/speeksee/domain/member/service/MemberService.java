package com._ithon.speeksee.domain.member.service;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	@Transactional(readOnly = true)
	public MemberInfoResponseDto getMyInfo(Member member) {
		return MemberInfoResponseDto.from(member);
	}

	@Transactional(readOnly = true)
	public Member findByEmail(String email) {
		return memberRepository.findByEmail(email)
			.orElseThrow(() -> new NoSuchElementException("존재하지 않는 이메일입니다: " + email));
	}
}
