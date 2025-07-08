package com._ithon.speeksee.domain.voicefeedback.statistics.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "주간 평균 정확도 DTO")
public record WeeklyAccuracyDto(

	@Schema(description = "주 시작일 (월요일 기준)", example = "2025-07-01")
	LocalDate weekStartDate,

	@Schema(description = "해당 주의 평균 정확도 (%)", example = "89.7")
	Double averageAccuracy

) {}
