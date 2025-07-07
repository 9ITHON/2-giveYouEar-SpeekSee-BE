package com._ithon.speeksee.domain.voicefeedback.practice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com._ithon.speeksee.domain.script.domain.Script;
import com._ithon.speeksee.domain.script.repository.ScriptRepository;
import com._ithon.speeksee.domain.member.entity.Member;
import com._ithon.speeksee.domain.member.repository.MemberRepository;
import com._ithon.speeksee.domain.voicefeedback.streaming.dto.response.WordInfoDto;
import com._ithon.speeksee.domain.voicefeedback.practice.entity.PracticeWord;
import com._ithon.speeksee.domain.voicefeedback.practice.entity.ScriptPractice;
import com._ithon.speeksee.domain.voicefeedback.practice.repository.ScriptPracticeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PracticeSaveService {

	private final MemberRepository memberRepository;
	private final ScriptRepository scriptRepository;
	private final ScriptPracticeRepository practiceRepository;

	@Transactional
	public void save(Long memberId, Long scriptId, String transcript, double accuracy, List<WordInfoDto> words) {
		// 1. 사용자 & 대본 조회
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

		Script script = scriptRepository.findById(scriptId)
			.orElseThrow(() -> new IllegalArgumentException("해당 대본이 존재하지 않습니다."));

		// 2. ScriptPractice 생성
		ScriptPractice practice = ScriptPractice.builder()
			.member(member)
			.script(script)
			.transcript(transcript)
			.accuracy(accuracy)
			.build();

		// 3. PracticeWord 리스트 추가
		for (WordInfoDto dto : words) {
			PracticeWord word = PracticeWord.builder()
				.word(dto.getWord())
				.startTime(dto.getStartTime())
				.endTime(dto.getEndTime())
				.isCorrect(dto.isCorrect())
				.build();

			practice.addPracticeWord(word); // 연관관계 편의 메서드
		}

		// 4. 저장
		practiceRepository.save(practice);
	}
}

