package com._ithon.speeksee.domain.Script.entity;

public enum DifficultyLevel {
	EASY("쉬움"),
	MEDIUM("중간"),
	HARD("어려움");

	private final String description;

	DifficultyLevel(String description) {
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
