package com._ithon.speeksee.domain.Script.service;

import org.springframework.stereotype.Service;

import com._ithon.speeksee.domain.Script.domain.DifficultyLevel;
import com._ithon.speeksee.domain.Script.domain.Script;
import com._ithon.speeksee.domain.Script.domain.ScriptCategory;
import com._ithon.speeksee.domain.Script.port.LlmClient;
import com._ithon.speeksee.domain.Script.repository.ScriptRepository;
import com._ithon.speeksee.domain.member.entity.Member;
import com._ithon.speeksee.domain.member.repository.MemberRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScriptService {

	private final LlmClient llmClient;
	private final ScriptRepository scriptRepository;
	private final MemberRepository memberRepository;

	/**
	 * 주어진 카테고리와 난이도에 맞는 대본을 생성합니다.
	 *
	 * @param category        대본 카테고리
	 * @param difficultyLevel 대본 난이도
	 * @return 생성된 대본
	 */
	@Transactional
	public Script createScript(ScriptCategory category, DifficultyLevel difficultyLevel, Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다."));

		String prompt = buildPrompt(category, difficultyLevel);
		String content = llmClient.chat(prompt);

		Script script = Script.builder()
			.title(category.getDescription() + " 대본")
			.content(content)
			.category(category)
			.difficultyLevel(difficultyLevel)
			.build();

		member.addScript(script); // 이제 영속 상태의 member라 문제 없음

		return scriptRepository.save(script);
	}

	/**
	 * 주어진 카테고리와 난이도에 맞는 대본을 생성하기 위한 프롬프트를 빌드합니다.
	 *
	 * @param category        대본 카테고리
	 * @param difficultyLevel 대본 난이도
	 * @return 생성된 프롬프트 문자열
	 */
	private String buildPrompt(ScriptCategory category, DifficultyLevel difficultyLevel) {
		return String.format(
			"다음 주제로 %s 수준의 한국어 대본을 생성해 주세요: %s",
			difficultyLevel.getDescription(),
			category.getDescription()
		);
	}
}
