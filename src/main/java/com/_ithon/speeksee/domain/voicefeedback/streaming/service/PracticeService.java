package com._ithon.speeksee.domain.voicefeedback.streaming.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com._ithon.speeksee.domain.voicefeedback.streaming.dto.response.PracticeResponse;
import com._ithon.speeksee.domain.voicefeedback.streaming.entity.ScriptPractice;
import com._ithon.speeksee.domain.voicefeedback.streaming.repository.ScriptPracticeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PracticeService {

	private final ScriptPracticeRepository practiceRepository;

	@Transactional(readOnly = true)
	public List<PracticeResponse> findByMemberId(Long memberId) {
		return practiceRepository.findAllByMemberId(memberId).stream()
			.map(PracticeResponse::fromEntity)
			.toList();
	}

	@Transactional(readOnly = true)
	public PracticeResponse findById(Long id) {
		return PracticeResponse.fromEntity(
			practiceRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("연습 기록이 존재하지 않습니다."))
		);
	}

	@Transactional
	public void deleteById(Long id, Long memberId) {
		ScriptPractice practice = practiceRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("연습 기록이 존재하지 않습니다."));

		if (!practice.getMember().getId().equals(memberId)) {
			throw new IllegalStateException("본인만 삭제할 수 있습니다.");
		}

		practiceRepository.delete(practice);
	}
}

