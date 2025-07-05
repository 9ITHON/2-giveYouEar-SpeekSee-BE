package com._ithon.speeksee.domain.Script.dto.resquest;

import com._ithon.speeksee.domain.Script.domain.DifficultyLevel;
import com._ithon.speeksee.domain.Script.domain.ScriptCategory;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ScriptGenerateRequest {

	@Schema(
		description = "대본 카테고리. 사용 가능한 값: NEWS(뉴스), WEATHER(날씨), SELF_INTRODUCTION(자기소개), DAILY(일상)",
		example = "NEWS"
	)
	@NotNull(message = "카테고리를 입력해 주세요.")
	private ScriptCategory category;

	@Schema(
		description = "대본 난이도. 사용 가능한 값: EASY(쉬움), MEDIUM(중간), HARD(어려움)",
		example = "EASY"
	)
	@NotNull(message = "난이도를 입력해 주세요.")
	private DifficultyLevel difficulty;
}