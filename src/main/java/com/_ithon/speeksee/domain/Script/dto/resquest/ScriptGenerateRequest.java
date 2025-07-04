package com._ithon.speeksee.domain.Script.dto.resquest;

import com._ithon.speeksee.domain.Script.domain.DifficultyLevel;
import com._ithon.speeksee.domain.Script.domain.ScriptCategory;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ScriptGenerateRequest {
	@NotNull
	private ScriptCategory category;

	@NotNull
	private DifficultyLevel difficulty;
}