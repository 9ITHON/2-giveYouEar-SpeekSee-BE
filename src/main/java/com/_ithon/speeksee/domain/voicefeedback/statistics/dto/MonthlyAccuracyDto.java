package com._ithon.speeksee.domain.voicefeedback.statistics.dto;

import java.time.YearMonth;

public record MonthlyAccuracyDto(
	YearMonth month,
	Double averageAccuracy
) {}