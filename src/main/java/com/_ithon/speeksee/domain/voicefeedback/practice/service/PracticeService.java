package com._ithon.speeksee.domain.voicefeedback.streaming.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com._ithon.speeksee.domain.member.entity.Member;
import com._ithon.speeksee.domain.member.repository.MemberRepository;
import com._ithon.speeksee.domain.voicefeedback.streaming.dto.response.PracticeResponse;
import com._ithon.speeksee.domain.voicefeedback.streaming.entity.ScriptPractice;
import com._ithon.speeksee.domain.voicefeedback.streaming.repository.ScriptPracticeRepository;
import com._ithon.speeksee.global.infra.exception.entityException.MemberNotFoundException;
import com._ithon.speeksee.global.infra.exception.entityException.PracticeNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PracticeService {

	private final ScriptPracticeRepository practiceRepository;
	private final MemberRepository memberRepository;

	@Transactional(readOnly = true)
	public List<PracticeResponse> findByMemberEmail(String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(MemberNotFoundException::new);

		return practiceRepository.findAllByMember(member).stream()
			.map(PracticeResponse::fromEntity)
			.toList();
	}

	@Transactional(readOnly = true)
	public PracticeResponse findById(Long id) {
		ScriptPractice practice = practiceRepository.findById(id)
			.orElseThrow(PracticeNotFoundException::new);
		return PracticeResponse.fromEntity(practice);
	}

	@Transactional
	public void deleteByEmail(Long practiceId, String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(MemberNotFoundException::new);

		ScriptPractice practice = practiceRepository.findById(practiceId)
			.orElseThrow(PracticeNotFoundException::new);

		if (!practice.getMember().equals(member)) {
			throw new AccessDeniedException("본인의 연습 기록만 삭제할 수 있습니다.");
		}

		practiceRepository.delete(practice);
	}
}


