package com._ithon.speeksee.domain.member.service;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com._ithon.speeksee.domain.member.controller.AdditionalInfoRequestDto;
import com._ithon.speeksee.domain.member.dto.request.SignUpRequestDto;
import com._ithon.speeksee.domain.member.dto.response.MemberInfoResponseDto;
import com._ithon.speeksee.domain.member.entity.Member;
import com._ithon.speeksee.domain.member.repository.MemberRepository;
import com._ithon.speeksee.global.infra.exception.auth.SpeekseeAuthException;
import com._ithon.speeksee.global.infra.exception.entityException.MemberNotFoundException;
import com.google.api.gax.rpc.NotFoundException;

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

	@Transactional
	public MemberInfoResponseDto completeAdditionalInfo(String email, AdditionalInfoRequestDto dto) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(MemberNotFoundException::new);
		member.completeAdditionalInfo(dto.getNickname(), dto.getBirthdate());
		return MemberInfoResponseDto.from(member);
	}

}
