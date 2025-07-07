package com._ithon.speeksee.domain.voicefeedback.streaming.dto.response;

import java.util.List;

public record PracticeChartResponse(
	String period,
	String unit,
	List<PracticeChartPoint> points
) {}
