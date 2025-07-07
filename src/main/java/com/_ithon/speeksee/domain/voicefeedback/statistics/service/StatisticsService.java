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
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.ScriptAccuracyDto;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.ScriptPracticeCountDto;
import com._ithon.speeksee.domain.voicefeedback.statistics.entity.PeriodType;
import com._ithon.speeksee.domain.voicefeedback.statistics.repository.ScriptPracticeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticsService {

	private final ScriptPracticeRepository practiceRepository;

	/**
	 * 특정 기간의 연습 통계를 가져옵니다.
	 *
	 * @param memberId 연습 통계를 조회할 멤버의 ID
	 * @param period   통계 기간 (예: "WEEKLY", "HALF_YEAR", "YEARLY")
	 * @return 연습 통계 응답 객체
	 */
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

	/**
	 * 주어진 연습 포인트 리스트를 누적하여 새로운 리스트를 반환합니다.
	 *
	 * @param points 연습 포인트 리스트
	 * @param start  시작 값
	 * @return 누적된 연습 포인트 리스트
	 */
	private List<PracticeChartPoint> accumulate(List<PracticeChartPoint> points, long start) {
		List<PracticeChartPoint> result = new ArrayList<>();
		long sum = start;
		for (PracticeChartPoint p : points) {
			sum += p.count();
			result.add(new PracticeChartPoint(p.label(), sum));
		}
		return result;
	}

	/**
	 * 요일을 한국어로 변환합니다.
	 *
	 * @param day 요일
	 * @return 한국어 요일 문자열
	 */
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

	/**
	 * 특정 멤버의 대본 연습 정확도 추세를 가져옵니다.
	 *
	 * @param memberId   멤버 ID
	 * @param periodType 기간 유형 (예: WEEKLY, HALF_YEAR, YEARLY)
	 * @return 대본 연습 정확도 추세 리스트
	 */
	public List<ScriptAccuracyDto> getScriptAccuracyTrends(Long memberId, PeriodType periodType) {
		return practiceRepository.findScriptAccuracyTrends(memberId, periodType);
	}

	/**
	 * 특정 멤버의 대본 연습 횟수를 기간별로 집계합니다.
	 *
	 * @param memberId   멤버 ID
	 * @param periodType 기간 유형 (예: WEEKLY, HALF_YEAR, YEARLY)
	 * @return 대본 연습 횟수 리스트
	 */
	public List<ScriptPracticeCountDto> getScriptPracticeCount(Long memberId, PeriodType periodType) {
		return practiceRepository.countScriptPracticeByPeriod(memberId, periodType);
	}

	/**
	 * 특정 멤버의 총 점수를 기간별로 계산합니다.
	 *
	 * @param memberId   멤버 ID
	 * @param periodType 기간 유형 (예: WEEKLY, HALF_YEAR, YEARLY)
	 * @return 총 점수 리스트
	 */
	public List<PracticeChartPoint> getTotalScoreOverTime(Long memberId, PeriodType periodType) {
		return practiceRepository.calculateTotalScoreOverTime(memberId, periodType);
	}
}
