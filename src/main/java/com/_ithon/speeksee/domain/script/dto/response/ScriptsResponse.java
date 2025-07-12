package com._ithon.speeksee.domain.script.dto.response;

import java.time.LocalDateTime;

import com._ithon.speeksee.domain.script.domain.DifficultyLevel;
import com._ithon.speeksee.domain.script.domain.Script;
import com._ithon.speeksee.domain.script.domain.ScriptCategory;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "스크립트 응답 DTO")
public record ScriptsResponse(
	@Schema(description = "스크립트 ID", example = "1")
	Long id,

	@Schema(description = "스크립트 제목", example = "자기소개 연습")
	String title,

	@Schema(description = "스크립트 내용", example = "안녕하세요, 저는 김규일입니다.")
	String content,

	@Schema(description = "스크립트 카테고리", example = "SELF_INTRODUCTION")
	ScriptCategory category,

	@Schema(description = "난이도", example = "MEDIUM")
	DifficultyLevel difficultyLevel,

	@Schema(description = "레벨 테스트 여부", example = "false")
	boolean isLevelTest,

	@Schema(description = "연습 횟수", example = "0")
	int practiceCount,

	@Schema(description = "생성 시각", example = "2025-07-10T13:00:00")
	LocalDateTime createdAt,

	@Schema(description = "마지막 수정 시각", example = "2025-07-10T13:00:00")
	LocalDateTime updatedAt
) {
	public static ScriptsResponse from(Script script) {
		return new ScriptsResponse(
			script.getId(),
			script.getTitle(),
			script.getContent(),
			script.getCategory(),
			script.getDifficultyLevel(),
			script.isLevelTest(),
			script.getPracticeCount(),
			script.getCreatedAt(),
			script.getUpdatedAt()
		);
	}
}