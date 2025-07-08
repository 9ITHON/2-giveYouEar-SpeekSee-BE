package com._ithon.speeksee.domain.voicefeedback.statistics.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "일별 누적 연습 횟수 DTO (1주 조회용)")
public class DailyPracticeCountDto {

	@Schema(description = "날짜", example = "2025-07-01")
	private LocalDate date;

	@Schema(description = "해당 날짜까지의 누적 연습 횟수", example = "7")
	private long cumulativeCount;
}