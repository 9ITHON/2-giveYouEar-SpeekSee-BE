package com._ithon.speeksee.domain.voicefeedback.statistics.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "연습 통계 차트 응답")
public record PracticeChartResponse(
	@Schema(description = "차트 기간 구분 (weekly, half-year, yearly)", example = "weekly")
	String period,

	@Schema(description = "단위 (요일: day, 주차: week, 월: month)", example = "day")
	String unit,

	@Schema(description = "전체 연습한 대본 수 (누적 고유 script 수)", example = "42")
	long totalCount,

	@Schema(description = "해당 기간의 각 시점별 개별 연습 수")
	List<PracticeChartPoint> points,

	@Schema(description = "해당 기간의 각 시점까지 누적된 연습 수")
	List<PracticeChartPoint> cumulativePoints
) {}
