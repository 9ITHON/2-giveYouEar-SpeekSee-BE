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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {
	private final AttendanceService attendanceService;

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
