package com._ithon.speeksee.domain.voicefeedback.statistics.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com._ithon.speeksee.domain.voicefeedback.statistics.dto.CumulativeScoreDto;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.DailyPracticeCountDto;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.MaxAccuracyTrendDto;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.MonthlyPracticeCountDto;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.PracticeCountResponse;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.WeeklyPracticeCountDto;
import com._ithon.speeksee.domain.voicefeedback.statistics.entity.PeriodType;
import com._ithon.speeksee.domain.voicefeedback.statistics.service.StatisticsService;
import com._ithon.speeksee.global.auth.model.CustomUserDetails;
import com._ithon.speeksee.global.infra.exception.response.ApiRes;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

	@GetMapping("/practice-counts/all")
	public ResponseEntity<ApiRes<List<PracticeCountResponse>>> getAllPeriodCounts(
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		Long memberId = userDetails.getMember().getId(); // 또는 userDetails.getId() 등
		List<PracticeCountResponse> response = statisticsService.getAllPeriodCumulativeCounts(memberId);
		return ResponseEntity.ok(ApiRes.success(response));
	}

	@Operation(summary = "정확도 변화 추이 조회", description = "scriptId를 기준으로 주어진 기간별 정확도 추이를 반환합니다.")
	@GetMapping("/accuracy-trend")
	public ApiRes<List<?>> getAccuracyTrend(
		@Parameter(description = "대본 ID", example = "1")
		@RequestParam Long scriptId,

		@Parameter(description = "조회 기간 (weekly, half-year, yearly)", example = "weekly")
		@RequestParam String period
	) {
		PeriodType periodType = PeriodType.fromString(period);
		List<?> result = statisticsService.getAccuracyTrend(scriptId, periodType);
		return ApiRes.success(result);
	}

	@GetMapping("/scripts/{scriptId}/max-accuracy-trend")
	public ResponseEntity<ApiRes<List<MaxAccuracyTrendDto>>> getMaxAccuracyTrend(@PathVariable Long scriptId) {
		List<MaxAccuracyTrendDto> result = statisticsService.getMaxAccuracyTrend(scriptId);
		return ResponseEntity.ok(ApiRes.success(result, "날짜별 누적 최대 정확도 조회 성공"));
	}

	@GetMapping("/me/cumulative-score")
	public ResponseEntity<ApiRes<List<CumulativeScoreDto>>> getAllCumulativeScore(@AuthenticationPrincipal CustomUserDetails userDetails) {
		Long memberId = userDetails.getMember().getId();
		List<CumulativeScoreDto> result = statisticsService.getAllPeriodCumulativeScores(memberId);
		return ResponseEntity.ok(ApiRes.success(result));
	}

}

