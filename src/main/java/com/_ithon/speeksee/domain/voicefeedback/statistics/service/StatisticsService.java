package com._ithon.speeksee.domain.voicefeedback.streaming.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com._ithon.speeksee.domain.voicefeedback.streaming.dto.response.PracticeChartPoint;
import com._ithon.speeksee.domain.voicefeedback.streaming.dto.response.PracticeChartResponse;
import com._ithon.speeksee.domain.voicefeedback.streaming.entity.PeriodType;
import com._ithon.speeksee.domain.voicefeedback.practice.repository.ScriptPracticeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticsService {

	private final ScriptPracticeRepository practiceRepository;

	public PracticeChartResponse getPracticeChart(Long memberId, String period) {
		PeriodType type = PeriodType.fromString(period);
		LocalDate today = LocalDate.now();
		LocalDateTime start = type.startDate(today);

		List<PracticeChartPoint> points;
		long initial;

		switch (type) {
			case WEEKLY -> {
				List<Object[]> rows = practiceRepository.countDailyPracticeLastWeek(memberId);
				Map<LocalDate, Long> dateToCount = rows.stream()
					.collect(Collectors.toMap(
						row -> ((java.sql.Date) row[0]).toLocalDate(),
						row -> ((Number) row[1]).longValue()
					));

				points = new ArrayList<>();
				for (int i = type.rangeCount() - 1; i >= 0; i--) {
					LocalDate date = today.minusDays(i);
					String label = getKoreanDayLabel(date.getDayOfWeek());
					long count = dateToCount.getOrDefault(date, 0L);
					points.add(new PracticeChartPoint(label, count));
				}
			}
			case HALF_YEAR -> {
				List<Object[]> rows = practiceRepository.countWeeklyPracticeLastSixMonths(memberId);
				points = rows.stream()
					.map(row -> new PracticeChartPoint("W" + row[0], ((Number) row[1]).longValue()))
					.toList();
			}
			case YEARLY -> {
				List<Object[]> rows = practiceRepository.countMonthlyPracticeLastYear(memberId);
				points = rows.stream()
					.map(row -> new PracticeChartPoint((String) row[0], ((Number) row[1]).longValue()))
					.toList();
			}
			default -> throw new IllegalStateException("Unexpected period: " + type);
		}

		initial = practiceRepository.countDistinctScriptsBeforeDate(memberId, start);

		return new PracticeChartResponse(
			type.label(),
			type.unitLabel(),
			initial + points.stream().mapToLong(PracticeChartPoint::count).sum(),
			points,
			accumulate(points, initial)
		);
	}

	private List<PracticeChartPoint> accumulate(List<PracticeChartPoint> points, long start) {
		List<PracticeChartPoint> result = new ArrayList<>();
		long sum = start;
		for (PracticeChartPoint p : points) {
			sum += p.count();
			result.add(new PracticeChartPoint(p.label(), sum));
		}
		return result;
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
}
