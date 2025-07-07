package com._ithon.speeksee.domain.voicefeedback.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlyPracticeCountDto {
	private int year;            // 예: 2025
	private int month;           // 1~12
	private long cumulativeCount;
}
