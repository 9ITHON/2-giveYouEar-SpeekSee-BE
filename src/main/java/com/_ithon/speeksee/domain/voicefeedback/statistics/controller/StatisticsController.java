package com._ithon.speeksee.domain.voicefeedback.statistics.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com._ithon.speeksee.domain.voicefeedback.statistics.dto.DailyPracticeCountDto;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.MonthlyPracticeCountDto;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.WeeklyPracticeCountDto;
import com._ithon.speeksee.domain.voicefeedback.statistics.entity.PeriodType;
import com._ithon.speeksee.domain.voicefeedback.statistics.service.StatisticsService;
import com._ithon.speeksee.global.infra.exception.response.ApiRes;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
public class StatisticsController {

	private final StatisticsService statisticsService;


	@GetMapping("/practice-count")
	public ResponseEntity<ApiRes<?>> getPracticeCountTrend(
		@RequestParam Long memberId,
		@RequestParam("period") String periodRaw
	) {
		PeriodType period = PeriodType.fromString(periodRaw);
		LocalDate baseDate = LocalDate.now();
		LocalDate startDate = period.startDate(baseDate).toLocalDate();

		return switch (period) {
			case WEEKLY -> {
				List<DailyPracticeCountDto> result = statisticsService.getDailyPracticeCounts(memberId, startDate, baseDate);
				yield ResponseEntity.ok(ApiRes.success(result));
			}
			case HALF_YEAR -> {
				List<WeeklyPracticeCountDto> result = statisticsService.getWeeklyPracticeCounts(memberId, startDate, baseDate);
				yield ResponseEntity.ok(ApiRes.success(result));
			}
			case YEARLY -> {
				List<MonthlyPracticeCountDto> result = statisticsService.getMonthlyPracticeCounts(memberId, startDate, baseDate);
				yield ResponseEntity.ok(ApiRes.success(result));
			}
		};
	}

}

