package com._ithon.speeksee.domain.voicefeedback.statistics.util;

import java.time.LocalDate;
import java.time.temporal.IsoFields;

import com._ithon.speeksee.domain.voicefeedback.statistics.entity.PeriodType;

public class LabelConverter {

	private LabelConverter() {
		throw new UnsupportedOperationException("Utility class");
	}

	public static String convertToLabel(PeriodType periodType, LocalDate date) {
		return switch (periodType) {
			case WEEKLY -> date.toString(); // e.g. "2025-07-07"
			case HALF_YEAR -> "W" + date.get(IsoFields.WEEK_BASED_YEAR)
				+ String.format("%02d", date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
			case YEARLY -> date.getYear() + "-" + String.format("%02d", date.getMonthValue());
		};
	}
}