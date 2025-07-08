package com._ithon.speeksee.domain.voicefeedback.statistics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "주간 누적 연습 횟수 DTO")
public class WeeklyPracticeCountDto {

	@Schema(description = "ISO 주차 기준 연도", example = "2025")
	private int year;

	@Schema(description = "ISO 주차 번호 (1~53)", example = "27")
	private int week;

	@Schema(description = "해당 주까지의 누적 연습 횟수", example = "21")
	private long cumulativeCount;
}