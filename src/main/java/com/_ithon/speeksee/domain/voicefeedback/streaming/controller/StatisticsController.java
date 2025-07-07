package com._ithon.speeksee.domain.voicefeedback.streaming.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com._ithon.speeksee.domain.member.entity.Member;
import com._ithon.speeksee.domain.voicefeedback.streaming.dto.response.PracticeChartResponse;
import com._ithon.speeksee.domain.voicefeedback.streaming.service.StatisticsService;
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
}

