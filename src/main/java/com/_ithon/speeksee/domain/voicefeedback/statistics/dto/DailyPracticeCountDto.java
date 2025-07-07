package com._ithon.speeksee.domain.voicefeedback.statistics.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

// 일별 응답 (1주)
@Data
@AllArgsConstructor
public class DailyPracticeCountDto {
	private LocalDate date;       // 예: 2025-07-01
	private long cumulativeCount; // 해당 일까지의 누적 연습 수
}