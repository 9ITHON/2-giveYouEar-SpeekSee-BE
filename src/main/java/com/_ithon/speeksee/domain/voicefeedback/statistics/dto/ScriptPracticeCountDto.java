package com._ithon.speeksee.domain.voicefeedback.statistics.dto;

import java.util.List;

public record ScriptPracticeCountDto(
	Long scriptId,
	String scriptTitle,
	List<PracticeChartPoint> points
) {}
