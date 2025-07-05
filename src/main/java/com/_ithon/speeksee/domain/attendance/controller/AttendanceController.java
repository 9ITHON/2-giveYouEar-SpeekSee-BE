package com._ithon.speeksee.domain.attendance.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com._ithon.speeksee.domain.attendance.dto.response.AttendanceStatusResponseDto;
import com._ithon.speeksee.domain.attendance.repository.AttendanceRepository;
import com._ithon.speeksee.domain.attendance.service.AttendanceService;
import com._ithon.speeksee.domain.member.entity.Member;
import com._ithon.speeksee.global.auth.model.CustomUserDetails;
import com._ithon.speeksee.global.infra.exception.response.ApiRes;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {
	private final AttendanceService attendanceService;

	@Operation(
		summary = "출석 캘린더 조회",
		description = "사용자의 연도 및 월별 출석 현황을 조회합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "출석체크 조회 성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = AttendanceStatusResponseDto.class),
				examples = @ExampleObject(
					name = "성공 예시",
					value = """
						{
						  "status": 200,
						  "message": "출석체크 조회",
						  "data": {
						    "year": 2025,
						    "month": 7,
						    "attendanceDates": [
						      "2025-07-05"
						    ]
						  }
						}
						"""
				)
			)
		)
	})
	@GetMapping("/calendar")
	public ResponseEntity<ApiRes<AttendanceStatusResponseDto>> getAttendanceCalendar(
		@RequestParam int year,
		@RequestParam int month,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		Member member = userDetails.getMember();
		AttendanceStatusResponseDto dto = attendanceService.getMonthlyAttendance(member, year, month);
		return ResponseEntity.ok(ApiRes.success(dto, "출석체크 조회"));
	}
}
