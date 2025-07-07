package com._ithon.speeksee.domain.voicefeedback.streaming.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com._ithon.speeksee.domain.voicefeedback.streaming.dto.response.PracticeChartPoint;
import com._ithon.speeksee.domain.voicefeedback.streaming.dto.response.PracticeChartResponse;
import com._ithon.speeksee.domain.voicefeedback.streaming.repository.ScriptPracticeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticsService {

	private final ScriptPracticeRepository practiceRepository;

	private PracticeChartResponse getWeekly(Long memberId, LocalDate today) {
		List<Object[]> rows = practiceRepository.countDailyPracticeLastWeek(memberId);

		Map<LocalDate, Long> dateToCount = rows.stream()
			.collect(Collectors.toMap(
				row -> ((java.sql.Date) row[0]).toLocalDate(),
				row -> ((Number) row[1]).longValue()
			));

		List<PracticeChartPoint> points = new ArrayList<>();
		for (int i = 6; i >= 0; i--) {
			LocalDate date = today.minusDays(i);
			String label = getKoreanDayLabel(date.getDayOfWeek());
			long count = dateToCount.getOrDefault(date, 0L);
			points.add(new PracticeChartPoint(label, count));
		}

		return new PracticeChartResponse("weekly", "day", points);
	}

	private String getKoreanDayLabel(DayOfWeek day) {
		return switch (day) {
			case MONDAY -> "월";
			case TUESDAY -> "화";
			case WEDNESDAY -> "수";
			case THURSDAY -> "목";
			case FRIDAY -> "금";
			case SATURDAY -> "토";
			case SUNDAY -> "일";
		};
	}

	public PracticeChartResponse getPracticeChart(Long memberId, String period) {
		LocalDate today = LocalDate.now();

		return switch (period) {
			case "weekly" -> getWeekly(memberId, today);
			case "half-year" -> getHalfYear(memberId);
			case "yearly" -> getYearly(memberId);
			default -> throw new IllegalArgumentException("지원하지 않는 기간: " + period);
		};
	}

	private PracticeChartResponse getHalfYear(Long memberId) {
		List<Object[]> rows = practiceRepository.countWeeklyPracticeLastSixMonths(memberId);
		List<PracticeChartPoint> points = rows.stream()
			.map(row -> new PracticeChartPoint("W" + row[0], ((Number) row[1]).longValue()))
			.toList();

		return new PracticeChartResponse("half-year", "week", points);
	}

	private PracticeChartResponse getYearly(Long memberId) {
		List<Object[]> rows = practiceRepository.countMonthlyPracticeLastYear(memberId);
		List<PracticeChartPoint> points = rows.stream()
			.map(row -> new PracticeChartPoint((String) row[0], ((Number) row[1]).longValue()))
			.toList();

		return new PracticeChartResponse("yearly", "month", points);
	}
}
