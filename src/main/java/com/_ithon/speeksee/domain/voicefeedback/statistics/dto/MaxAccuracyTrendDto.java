package com._ithon.speeksee.domain.voicefeedback.statistics.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "날짜별 누적 최대 정확도 추이 DTO")
public record MaxAccuracyTrendDto(

	@Schema(description = "날짜 (기간 단위: 일)", example = "2025-07-08")
	LocalDate period,

	@Schema(description = "해당 날짜까지의 누적 최대 정확도", example = "91.2")
	Double maxAccuracySoFar

) {}
