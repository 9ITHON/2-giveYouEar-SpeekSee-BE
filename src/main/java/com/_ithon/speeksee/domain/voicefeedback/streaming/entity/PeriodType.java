package com._ithon.speeksee.domain.voicefeedback.streaming.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public enum PeriodType {
	WEEKLY("weekly", "day", 7),
	HALF_YEAR("half-year", "week", 26),
	YEARLY("yearly", "month", 12);

	private final String label;
	private final String unitLabel;
	private final int rangeCount;

	PeriodType(String label, String unitLabel, int rangeCount) {
		this.label = label;
		this.unitLabel = unitLabel;
		this.rangeCount = rangeCount;
	}

	public String label() {
		return label;
	}

	public String unitLabel() {
		return unitLabel;
	}

	public int rangeCount() {
		return rangeCount;
	}

	public LocalDateTime startDate(LocalDate baseDate) {
		return switch (this) {
			case WEEKLY -> baseDate.minusDays(rangeCount - 1).atStartOfDay();
			case HALF_YEAR -> baseDate.minusMonths(6).atStartOfDay();
			case YEARLY -> baseDate.minusYears(1).atStartOfDay();
		};
	}

	public static PeriodType fromString(String value) {
		return switch (value) {
			case "weekly" -> WEEKLY;
			case "half-year" -> HALF_YEAR;
			case "yearly" -> YEARLY;
			default -> throw new IllegalArgumentException("지원하지 않는 기간: " + value);
		};
	}
}
