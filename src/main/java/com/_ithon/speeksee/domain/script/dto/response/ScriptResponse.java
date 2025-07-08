package com._ithon.speeksee.domain.script.dto.response;

import com._ithon.speeksee.domain.script.domain.DifficultyLevel;
import com._ithon.speeksee.domain.script.domain.Script;
import com._ithon.speeksee.domain.script.domain.ScriptCategory;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ScriptResponse {

	@Schema(description = "스크립트 ID", example = "1")
	private Long id;

	@Schema(description = "스크립트 제목", example = "뉴스 대본")
	private String title;

	@Schema(description = "스크립트 본문 내용", example = "오늘의 주요 뉴스는...")
	private String content;

	@Schema(description = "스크립트 카테고리", example = "NEWS")
	private ScriptCategory category;

	@Schema(description = "스크립트 난이도", example = "EASY")
	private DifficultyLevel difficulty;

	@Schema(description = "작성자 이메일", example = "user@example.com")
	private String authorEmail;

	@Schema(description = "스크립트 누적 연습 횟수", example = "7")
	private int practiceCount;

	public static ScriptResponse from(Script script) {
		return ScriptResponse.builder()
			.id(script.getId())
			.title(script.getTitle())
			.content(script.getContent())
			.category(script.getCategory())
			.difficulty(script.getDifficultyLevel())
			.authorEmail(script.getAuthor().getEmail())
			.practiceCount(script.getPracticeCount())
			.build();
	}
}
