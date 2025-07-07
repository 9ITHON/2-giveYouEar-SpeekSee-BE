package com._ithon.speeksee.domain.voicefeedback.statistics.dto;

import java.util.List;

public record ScriptAccuracyDto(
	Long scriptId,
	String scriptTitle,
	List<PracticeChartPoint> points
) {}
