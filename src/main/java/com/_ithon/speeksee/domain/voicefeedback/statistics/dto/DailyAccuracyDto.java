package com._ithon.speeksee.domain.voicefeedback.statistics.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "날짜별 평균 정확도 DTO")
public record DailyAccuracyDto(

	@Schema(description = "날짜", example = "2025-07-08")
	LocalDate date,

	@Schema(description = "해당 날짜의 평균 정확도 (0.0 ~ 100.0)", example = "87.5")
	Double averageAccuracy

) {}