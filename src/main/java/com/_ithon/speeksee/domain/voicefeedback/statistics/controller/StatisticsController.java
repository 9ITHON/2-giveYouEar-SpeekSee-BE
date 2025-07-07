package com._ithon.speeksee.domain.voicefeedback.statistics.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com._ithon.speeksee.domain.member.entity.Member;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.PracticeChartPoint;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.PracticeChartResponse;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.ScriptAccuracyDto;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.ScriptPracticeCountDto;
import com._ithon.speeksee.domain.voicefeedback.statistics.entity.PeriodType;
import com._ithon.speeksee.domain.voicefeedback.statistics.service.StatisticsService;
import com._ithon.speeksee.global.auth.model.CustomUserDetails;
import com._ithon.speeksee.global.infra.exception.response.ApiRes;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
public class StatisticsController {

	private final StatisticsService statisticsService;

	@Operation(
		summary = "연습한 대본 수 차트 조회",
		description = """
			사용자의 최근 연습 기록을 차트 형식으로 반환합니다.
			
			- `weekly`: 최근 7일 (요일별)
			- `half-year`: 최근 6개월 (주차별)
			- `yearly`: 최근 1년 (월별)
			""",
		responses = {
			@ApiResponse(responseCode = "200", description = "성공적으로 조회됨"),
			@ApiResponse(responseCode = "400", description = "지원하지 않는 기간", content = @Content(schema = @Schema(implementation = ApiRes.class)))
		}
	)
	@GetMapping("/practices/chart")
	public ResponseEntity<ApiRes<PracticeChartResponse>> getPracticeChart(
		@Parameter(description = "기간 구분 (weekly, half-year, yearly)", example = "weekly")
		@RequestParam(defaultValue = "weekly") String period,

		@Parameter(hidden = true)
		@AuthenticationPrincipal Member member
	) {
		PracticeChartResponse response = statisticsService.getPracticeChart(member.getId(), period);
		return ResponseEntity.ok(ApiRes.success(response));
	}

	@Operation(summary = "대본별 정확도 변화 조회", description = "기간(weekly, half-year, yearly) 동안 대본별 정확도 변화를 반환합니다.")
	@GetMapping("/accuracy-trends")
	public ResponseEntity<ApiRes<List<ScriptAccuracyDto>>> getAccuracyTrends(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(name = "period") String periodRaw
	) {
		PeriodType period = PeriodType.fromString(periodRaw); // "weekly", "half-year", "yearly"
		List<ScriptAccuracyDto> result = statisticsService.getScriptAccuracyTrends(userDetails.getUserId(), period);
		return ResponseEntity.ok(ApiRes.success(result));
	}

	@Operation(summary = "대본별 연습 횟수 조회", description = "기간(weekly, half-year, yearly) 동안 대본별 연습 횟수를 반환합니다.")
	@GetMapping("/practice-count")
	public ResponseEntity<ApiRes<List<ScriptPracticeCountDto>>> getPracticeCounts(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(name = "period") String periodRaw
	) {
		PeriodType period = PeriodType.fromString(periodRaw);
		List<ScriptPracticeCountDto> result = statisticsService.getScriptPracticeCount(userDetails.getUserId(), period);
		return ResponseEntity.ok(ApiRes.success(result));
	}

	@Operation(summary = "대본별 통합 점수 변화 조회", description = "기간(weekly, half-year, yearly)에 따른 통합 점수 변화 (정확도 * 난이도) 누적 차트를 반환합니다.")
	@GetMapping("/total-score")
	public ResponseEntity<ApiRes<List<PracticeChartPoint>>> getTotalScore(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(name = "period") String periodRaw
	) {
		PeriodType period = PeriodType.fromString(periodRaw);
		List<PracticeChartPoint> result = statisticsService.getTotalScoreOverTime(userDetails.getUserId(), period);
		return ResponseEntity.ok(ApiRes.success(result));
	}
}

