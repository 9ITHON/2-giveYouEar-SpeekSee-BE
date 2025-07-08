package com._ithon.speeksee.domain.voicefeedback.statistics.dto;

import java.time.YearMonth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "월별 평균 정확도 DTO")
public record MonthlyAccuracyDto(

	@Schema(description = "월 (YYYY-MM 형식)", example = "2025-06")
	YearMonth month,

	@Schema(description = "해당 월의 평균 정확도", example = "84.3")
	Double averageAccuracy

) {}