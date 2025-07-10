package com._ithon.speeksee.domain.script.dto.resquest;

import com._ithon.speeksee.domain.script.domain.DifficultyLevel;
import com._ithon.speeksee.domain.script.domain.ScriptCategory;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "스크립트 일괄 등록 요청 DTO")
public record ScriptBatchSaveReq(
	@Schema(description = "스크립트 제목", example = "자기소개 연습용")
	@NotBlank(message = "제목은 필수입니다.")
	String title,

	@Schema(description = "스크립트 내용", example = "안녕하세요, 저는 김규일입니다.")
	@NotBlank(message = "내용은 필수입니다.")
	String content,

	@Schema(description = "스크립트 카테고리", example = "SELF_INTRODUCTION")
	@NotNull(message = "카테고리를 선택해주세요.")
	ScriptCategory category,

	@Schema(description = "난이도", example = "MEDIUM")
	@NotNull(message = "난이도를 선택해주세요.")
	DifficultyLevel difficultyLevel,

	@Schema(description = "레벨 테스트 여부", example = "false")
	boolean isLevelTest
) {}