package com._ithon.speeksee.domain.Script.domain;

public enum ScriptCategory {
	NEWS("뉴스"),
	WEATHER("날씨"),
	SELF_INTRODUCTION("자기소개"),
	DAILY("일상");

	private final String description;

	ScriptCategory(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return description;
	}
}
