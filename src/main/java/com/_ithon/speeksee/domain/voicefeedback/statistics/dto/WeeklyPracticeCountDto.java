package com._ithon.speeksee.domain.voicefeedback.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeeklyPracticeCountDto {
	private int year;             // ISO 주차 연도 (예: 2025)
	private int week;            // ISO 주차 번호 (1~52)
	private long cumulativeCount;
}