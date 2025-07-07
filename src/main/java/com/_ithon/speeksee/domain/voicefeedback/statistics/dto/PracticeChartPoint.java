package com._ithon.speeksee.domain.voicefeedback.statistics.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "차트 포인트 (날짜 라벨과 값)")
public record PracticeChartPoint(
	@Schema(description = "레이블 (예: 월, W202426, 2024-07)")
	String label,
	@Schema(description = "해당 시점의 개수")
	long count
) {}
