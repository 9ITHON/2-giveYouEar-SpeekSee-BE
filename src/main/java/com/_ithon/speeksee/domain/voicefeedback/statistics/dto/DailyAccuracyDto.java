package com._ithon.speeksee.domain.voicefeedback.statistics.dto;

import java.time.LocalDate;

public record DailyAccuracyDto(
	LocalDate date,
	Double averageAccuracy
) {}
