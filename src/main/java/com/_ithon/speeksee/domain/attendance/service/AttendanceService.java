package com._ithon.speeksee.domain.attendance.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com._ithon.speeksee.domain.attendance.dto.response.AttendanceStatusResponseDto;
import com._ithon.speeksee.domain.attendance.entity.Attendance;
import com._ithon.speeksee.domain.attendance.repository.AttendanceRepository;
import com._ithon.speeksee.domain.member.entity.Member;
import com._ithon.speeksee.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceService {

	private final AttendanceRepository attendanceRepository;
	private final MemberRepository memberRepository;

	public void attend(Member member) {
		LocalDate today = LocalDate.now();
		boolean alreadyAttended = attendanceRepository.existsByMemberAndDate(member, today);

		if (!alreadyAttended) { // 출석이 안되어있으면
			Attendance attendance = Attendance.builder()
				.member(member)
				.date(today)
				.build();

			attendanceRepository.save(attendance);
		}

		// 마지막 출석일
		LocalDate lastLogin = member.getLastLogin();

		if (lastLogin != null && lastLogin.plusDays(1).equals(today)) {
			// 어제도 로그인 → 연속 출석 유지
			int currentStreak = member.getConsecutiveDays() != null ? member.getConsecutiveDays() : 0;
			member.setConsecutiveDays(currentStreak + 1);
		} else {
			// 끊겼거나 첫 출석 → 1로 초기화
			member.setConsecutiveDays(1);
		}

		// 마지막 로그인 날짜 갱신
		member.setLastLogin(today);

		memberRepository.save(member);

	}

	public AttendanceStatusResponseDto getMonthlyAttendance(Member member, int year, int month) {
		LocalDate start = LocalDate.of(year, month, 1);
		LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

		List<LocalDate> dates = attendanceRepository
			.findByMemberAndDateBetween(member, start, end)
			.stream()
			.map(Attendance::getDate)
			.collect(Collectors.toList());

		return AttendanceStatusResponseDto.builder()
			.year(year)
			.month(month)
			.attendanceDates(dates)
			.build();
	}
}
