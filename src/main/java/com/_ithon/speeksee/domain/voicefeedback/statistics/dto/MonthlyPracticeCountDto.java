package com._ithon.speeksee.domain.voicefeedback.statistics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "월별 누적 연습 횟수 DTO")
public class MonthlyPracticeCountDto {

	@Schema(description = "연도 (예: 2025)", example = "2025")
	private int year;

	@Schema(description = "월 (1~12)", example = "7")
	private int month;

	@Schema(description = "해당 월까지의 누적 연습 횟수", example = "35")
	private long cumulativeCount;
}
