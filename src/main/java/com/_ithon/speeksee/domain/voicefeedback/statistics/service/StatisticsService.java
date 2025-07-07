package com._ithon.speeksee.domain.voicefeedback.statistics.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com._ithon.speeksee.domain.voicefeedback.statistics.dto.PracticeChartPoint;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.PracticeChartResponse;
import com._ithon.speeksee.domain.voicefeedback.statistics.entity.PeriodType;
import com._ithon.speeksee.domain.voicefeedback.statistics.repository.ScriptPracticeRepository;

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
				List<PracticeChartPoint> rawPoints = practiceRepository.countDailyPracticeLastWeek(memberId);

				// label이 요일명이 아니므로 변환 필요
				Map<String, Long> dateToCount = rawPoints.stream()
					.collect(Collectors.toMap(PracticeChartPoint::label, PracticeChartPoint::count));

				points = new ArrayList<>();
				for (int i = type.rangeCount() - 1; i >= 0; i--) {
					LocalDate date = today.minusDays(i);
					String dateKey = date.toString(); // e.g., "2024-07-07"
					String label = getKoreanDayLabel(date.getDayOfWeek());
					long count = dateToCount.getOrDefault(dateKey, 0L);
					points.add(new PracticeChartPoint(label, count));
				}
			}
			case HALF_YEAR -> {
				points = practiceRepository.countWeeklyPracticeLastSixMonths(memberId).stream()
					.map(p -> new PracticeChartPoint("W" + p.label(), p.count()))
					.toList();
			}
			case YEARLY -> {
				points = practiceRepository.countMonthlyPracticeLastYear(memberId);
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
