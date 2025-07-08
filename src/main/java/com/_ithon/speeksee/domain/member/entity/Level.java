package com._ithon.speeksee.domain.member.entity;

public enum Level {
	Beginner,
	Intermediate,
	Advanced;

	public static Level determineLevel(double accuracy) {
		double score = accuracy * 100;
		if (score >= 90.0)
			return Advanced;
		if (score >= 80.0)
			return Intermediate;
		return Beginner;
	}
}
