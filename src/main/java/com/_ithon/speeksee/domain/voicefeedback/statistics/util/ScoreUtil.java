package com._ithon.speeksee.domain.voicefeedback.statistics.util;

import com._ithon.speeksee.domain.script.domain.DifficultyLevel;

public class ScoreUtil {
	private ScoreUtil() {}

	public static int difficultyToScore(DifficultyLevel level) {
		return switch (level) {
			case EASY -> 1;
			case MEDIUM -> 2;
			case HARD -> 3;
		};
	}
}
