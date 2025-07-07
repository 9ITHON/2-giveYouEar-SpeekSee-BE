package com._ithon.speeksee.domain.voicefeedback.statistics.dto;

import java.time.LocalDate;

public record WeeklyAccuracyDto(
	LocalDate weekStartDate,
	Double averageAccuracy
) {}
