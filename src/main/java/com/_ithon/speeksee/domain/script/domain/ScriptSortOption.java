package com._ithon.speeksee.domain.script.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "스크립트 정렬 기준")
public enum ScriptSortOption {

	@Schema(description = "생성일 오름차순 (오래된 순)")
	CREATED_ASC,

	@Schema(description = "생성일 내림차순 (최신 순)")
	CREATED_DESC,

	@Schema(description = "마지막 연습일 오름차순")
	UPDATED_ASC,

	@Schema(description = "마지막 연습일 내림차순")
	UPDATED_DESC,

	@Schema(description = "연습 횟수 오름차순")
	COUNT_ASC,

	@Schema(description = "연습 횟수 내림차순")
	COUNT_DESC,

	@Schema(description = "제목 오름차순 (가나다순)")
	TITLE_ASC,

	@Schema(description = "제목 내림차순 (역순)")
	TITLE_DESC,

	@Schema(description = "난이도 오름차순 (쉬움 → 어려움)")
	DIFFICULTY_ASC,

	@Schema(description = "난이도 내림차순 (어려움 → 쉬움)")
	DIFFICULTY_DESC
}
