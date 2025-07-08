package com._ithon.speeksee.domain.voicefeedback.statistics.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "전체 기간 누적 연습 횟수 응답 DTO")
public record PracticeCountResponse(

	@Schema(description = "날짜", example = "2025-07-08")
	LocalDate date,

	@Schema(description = "해당 날짜까지의 누적 연습 횟수", example = "42")
	long cumulativeCount

) {}
