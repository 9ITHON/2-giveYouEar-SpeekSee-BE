package com._ithon.speeksee.domain.attendance.dto.response;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AttendanceStatusResponseDto {
	private int year;
	private int month;
	private List<LocalDate> attendanceDates;
}
