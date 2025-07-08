package com._ithon.speeksee.domain.voicefeedback.statistics.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "날짜별 누적 성취도 점수 DTO")
public record CumulativeScoreDto(

	@Schema(description = "날짜 (연습이 있었던 날)", example = "2025-07-08")
	LocalDate date,

	@Schema(description = "해당 날짜까지의 누적 점수", example = "27.5")
	double cumulativeScore

) {}