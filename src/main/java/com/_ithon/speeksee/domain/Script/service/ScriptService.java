package com._ithon.speeksee.domain.Script.service;

import org.springframework.stereotype.Service;

import com._ithon.speeksee.domain.Script.domain.DifficultyLevel;
import com._ithon.speeksee.domain.Script.domain.Script;
import com._ithon.speeksee.domain.Script.domain.ScriptCategory;
import com._ithon.speeksee.domain.Script.port.LlmClient;
import com._ithon.speeksee.domain.Script.repository.ScriptRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScriptService {

	private final LlmClient llmClient;
	private final ScriptRepository scriptRepository;

	/**
	 * 주어진 카테고리와 난이도에 맞는 대본을 생성합니다.
	 *
	 * @param category        대본 카테고리
	 * @param difficultyLevel 대본 난이도
	 * @return 생성된 대본
	 */
	@Transactional
	public Script createScript(ScriptCategory category, DifficultyLevel difficultyLevel) {
		String prompt = buildPrompt(category, difficultyLevel);
		String content = llmClient.chat(prompt);

		Script script = Script.builder()
			.title(category.getDescription() + " 대본")
			.content(content)
			.category(category)
			.difficultyLevel(difficultyLevel)
			.author("system") // TODO: 유저 연동 시 수정
			.build();

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
