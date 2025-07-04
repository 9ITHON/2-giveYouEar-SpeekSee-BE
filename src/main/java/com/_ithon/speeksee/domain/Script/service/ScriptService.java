package com._ithon.speeksee.domain.Script.service;

import org.springframework.stereotype.Service;

import com._ithon.speeksee.domain.Script.domain.DifficultyLevel;
import com._ithon.speeksee.domain.Script.domain.Script;
import com._ithon.speeksee.domain.Script.domain.ScriptCategory;
import com._ithon.speeksee.domain.Script.port.LlmClient;
import com._ithon.speeksee.domain.Script.repository.ScriptRepository;
import com._ithon.speeksee.domain.member.entity.Member;
import com._ithon.speeksee.domain.member.repository.MemberRepository;
import com._ithon.speeksee.global.infra.exception.entityException.MemberNotFoundException;

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
			.orElseThrow(MemberNotFoundException::new);

		String prompt = buildPrompt(category, difficultyLevel);

		int maxTokens = switch (difficultyLevel) {
			case EASY -> 300;
			case MEDIUM -> 500;
			case HARD -> 700;
		};

		String content = llmClient.chat(prompt, maxTokens);

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
		return String.format("""
        한국어 발음 연습용 스크립트를 작성해 주세요.

        - 사용자가 그대로 읽을 수 있도록 구성해 주세요.
        - 문장은 자연스럽고 발음 피드백 학습에 적합해야 합니다.
        - 대본은 단락 구분 없이 하나의 글로 출력해 주세요. 설명이나 부연 없이 순수한 대본만 제공해 주세요.
        - 주제: %s

        난이도 수준은 아래 기준에 맞춰주세요.
        - 쉬움: 초등학생도 이해할 수 있는 쉬운 어휘와 짧은 문장
        - 중간: 일상적인 대화 수준, 자연스럽고 약간의 복문 포함
        - 어려움: 뉴스나 발표체에 가까운 긴 문장과 복잡한 어휘

        난이도 수준: %s
        문장 수: 5문장 내외

        이제 실제 스크립트를 작성해 주세요.
        """,
			difficultyLevel.getDescription(),
			category.getDescription()
		);
	}
}
