package com._ithon.speeksee.domain.Script.dto.response;

import com._ithon.speeksee.domain.Script.domain.DifficultyLevel;
import com._ithon.speeksee.domain.Script.domain.Script;
import com._ithon.speeksee.domain.Script.domain.ScriptCategory;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ScriptResponse {
	private Long id;
	private String title;
	private String content;
	private ScriptCategory category;
	private DifficultyLevel difficulty;
	private String authorEmail;

	public static ScriptResponse from(Script script) {
		return ScriptResponse.builder()
			.id(script.getId())
			.title(script.getTitle())
			.content(script.getContent())
			.category(script.getCategory())
			.difficulty(script.getDifficultyLevel())
			.authorEmail(script.getAuthor().getEmail())
			.build();
	}
}
