package com._ithon.speeksee.domain.voicefeedback.statistics.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com._ithon.speeksee.domain.voicefeedback.statistics.dto.CumulativeScoreDto;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.DailyAccuracyDto;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.DailyPracticeCountDto;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.MaxAccuracyTrendDto;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.MonthlyAccuracyDto;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.MonthlyPracticeCountDto;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.PracticeCountResponse;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.WeeklyAccuracyDto;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.WeeklyPracticeCountDto;
import com._ithon.speeksee.domain.voicefeedback.statistics.entity.PeriodType;
import com._ithon.speeksee.domain.voicefeedback.statistics.repository.ScriptPracticeRepository;
import com.querydsl.core.Tuple;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticsService {

	private final ScriptPracticeRepository practiceRepository;

	public List<DailyPracticeCountDto> getDailyPracticeCounts(Long memberId, LocalDate startDate, LocalDate endDate) {
		// 1. 리포지토리에서 날짜별 연습 횟수 가져오기
		List<Tuple> result = practiceRepository.countDailyByMember(startDate, endDate, memberId);

		// 2. 날짜별 raw count 매핑
		Map<LocalDate, Long> dateToCountMap = result.stream()
			.collect(Collectors.toMap(
				t -> t.get(0, java.sql.Date.class).toLocalDate(),
				t -> t.get(1, Long.class)
			));

		// 3. 누락된 날짜 채우면서 누적값 계산
		List<DailyPracticeCountDto> cumulativeList = new ArrayList<>();
		long cumulative = 0L;

		for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
			long todayCount = dateToCountMap.getOrDefault(date, 0L);
			cumulative += todayCount;
			cumulativeList.add(new DailyPracticeCountDto(date, cumulative));
		}

		return cumulativeList;
	}

	public List<WeeklyPracticeCountDto> getWeeklyPracticeCounts(Long memberId, LocalDate startDate, LocalDate endDate) {
		List<Tuple> result = practiceRepository.countWeeklyByMember(startDate, endDate, memberId);

		// (year, week) → count 매핑
		Map<YearWeek, Long> countMap = result.stream()
			.collect(Collectors.toMap(
				t -> new YearWeek(
					t.get(0, Integer.class),
					t.get(1, Integer.class)
				),
				t -> t.get(2, Long.class)
			));

		// 기준 주 계산 (ISO 기준)
		List<YearWeek> weekList = new ArrayList<>();
		LocalDate cursor = startDate;
		while (!cursor.isAfter(endDate)) {
			YearWeek yw = YearWeek.from(cursor);
			if (!weekList.contains(yw)) weekList.add(yw);
			cursor = cursor.plusWeeks(1);
		}

		// 누적 합계 계산
		List<WeeklyPracticeCountDto> resultList = new ArrayList<>();
		long cumulative = 0L;
		for (YearWeek yw : weekList) {
			cumulative += countMap.getOrDefault(yw, 0L);
			resultList.add(new WeeklyPracticeCountDto(yw.year(), yw.week(), cumulative));
		}
		return resultList;
	}

	public List<MonthlyPracticeCountDto> getMonthlyPracticeCounts(Long memberId, LocalDate startDate, LocalDate endDate) {
		List<Tuple> result = practiceRepository.countMonthlyByMember(startDate, endDate, memberId);

		Map<YearMonth, Long> countMap = result.stream()
			.collect(Collectors.toMap(
				t -> YearMonth.of(t.get(0, Integer.class), t.get(1, Integer.class)),
				t -> t.get(2, Long.class)
			));

		// 월 리스트 생성
		List<YearMonth> monthList = new ArrayList<>();
		YearMonth cursor = YearMonth.from(startDate);
		YearMonth end = YearMonth.from(endDate);

		while (!cursor.isAfter(end)) {
			monthList.add(cursor);
			cursor = cursor.plusMonths(1);
		}

		// 누적 합계 계산
		List<MonthlyPracticeCountDto> resultList = new ArrayList<>();
		long cumulative = 0L;
		for (YearMonth ym : monthList) {
			cumulative += countMap.getOrDefault(ym, 0L);
			resultList.add(new MonthlyPracticeCountDto(ym.getYear(), ym.getMonthValue(), cumulative));
		}
		return resultList;
	}

	public List<PracticeCountResponse> getAllPeriodCumulativeCounts(Long memberId) {
		List<Tuple> result = practiceRepository.countDailyByMemberAllPeriod(memberId);

		// [1] Tuple -> Map<LocalDate, Long>
		Map<LocalDate, Long> dateToCount = result.stream()
			.collect(Collectors.toMap(
				t -> t.get(0, java.sql.Date.class).toLocalDate(),
				t -> t.get(1, Long.class)
			));

		// [2] 가장 오래된 날짜 ~ 오늘까지 모든 날짜 생성
		LocalDate startDate = dateToCount.keySet().stream().min(LocalDate::compareTo).orElse(LocalDate.now());
		LocalDate endDate = LocalDate.now();

		// [3] 누적 합산 리스트 만들기
		List<PracticeCountResponse> cumulativeList = new ArrayList<>();
		long cumulative = 0;

		for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
			cumulative += dateToCount.getOrDefault(date, 0L);
			cumulativeList.add(new PracticeCountResponse(date, cumulative));
		}

		return cumulativeList;
	}

	public record YearWeek(int year, int week) {
		public static YearWeek from(LocalDate date) {
			WeekFields wf = WeekFields.ISO;
			return new YearWeek(date.get(wf.weekBasedYear()), date.get(wf.weekOfWeekBasedYear()));
		}
	}

	public List<?> getAccuracyTrend(Long scriptId, PeriodType periodType) {
		LocalDate today = LocalDate.now();
		LocalDateTime startDateTime = periodType.startDate(today);
		LocalDate startDate = startDateTime.toLocalDate();
		LocalDate endDate = today;

		return switch (periodType) {
			case WEEKLY -> {
				var raw = practiceRepository.findDailyAccuracy(scriptId, startDate, endDate);
				yield fillMissingDates(startDate, endDate, raw); // 누락 보간
			}
			case HALF_YEAR -> {
				var raw = practiceRepository.findWeeklyAccuracy(scriptId, startDate, endDate);
				yield fillMissingWeeks(startDate, endDate, raw);
			}
			case YEARLY -> {
				var raw = practiceRepository.findMonthlyAccuracy(scriptId, startDate, endDate);
				yield fillMissingMonths(startDate, endDate, raw);
			}
		};
	}

	private List<DailyAccuracyDto> fillMissingDates(LocalDate from, LocalDate to, List<DailyAccuracyDto> rawData) {
		Map<LocalDate, Double> dataMap = rawData.stream()
			.collect(Collectors.toMap(DailyAccuracyDto::date, DailyAccuracyDto::averageAccuracy));

		List<DailyAccuracyDto> result = new ArrayList<>();
		for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
			result.add(new DailyAccuracyDto(
				date,
				dataMap.getOrDefault(date, null)  // null 처리 가능
			));
		}

		return result;
	}

	private List<WeeklyAccuracyDto> fillMissingWeeks(LocalDate from, LocalDate to, List<WeeklyAccuracyDto> rawData) {
		Map<LocalDate, Double> dataMap = rawData.stream()
			.collect(Collectors.toMap(WeeklyAccuracyDto::weekStartDate, WeeklyAccuracyDto::averageAccuracy));

		List<WeeklyAccuracyDto> result = new ArrayList<>();
		LocalDate cursor = from.with(java.time.DayOfWeek.MONDAY);

		while (!cursor.isAfter(to)) {
			result.add(new WeeklyAccuracyDto(
				cursor,
				dataMap.getOrDefault(cursor, null)
			));
			cursor = cursor.plusWeeks(1);
		}
		return result;
	}

	private List<MonthlyAccuracyDto> fillMissingMonths(LocalDate from, LocalDate to, List<MonthlyAccuracyDto> rawData) {
		Map<YearMonth, Double> dataMap = rawData.stream()
			.collect(Collectors.toMap(MonthlyAccuracyDto::month, MonthlyAccuracyDto::averageAccuracy));

		List<MonthlyAccuracyDto> result = new ArrayList<>();
		YearMonth cursor = YearMonth.from(from);
		YearMonth end = YearMonth.from(to);

		while (!cursor.isAfter(end)) {
			result.add(new MonthlyAccuracyDto(
				cursor,
				dataMap.getOrDefault(cursor, null)
			));
			cursor = cursor.plusMonths(1);
		}
		return result;
	}

	public List<MaxAccuracyTrendDto> getMaxAccuracyTrend(Long scriptId) {
		return practiceRepository.findDailyMaxAccuracyByScript(scriptId);
	}

	public List<CumulativeScoreDto> getAllPeriodCumulativeScores(Long memberId) {
		List<Tuple> dailyScores = practiceRepository.findDailyScoreByMemberAllPeriod(memberId);

		List<CumulativeScoreDto> result = new ArrayList<>();
		double cumulative = 0.0;

		for (Tuple tuple : dailyScores) {
			java.sql.Date sqlDate = tuple.get(0, java.sql.Date.class);
			Double score = tuple.get(1, Double.class);

			if (sqlDate != null && score != null) {
				cumulative += score;
				result.add(new CumulativeScoreDto(sqlDate.toLocalDate(), cumulative));
			}
		}
		return result;
	}

}
