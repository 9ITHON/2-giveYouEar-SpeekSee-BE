package com._ithon.speeksee.domain.voicefeedback.statistics.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com._ithon.speeksee.domain.voicefeedback.statistics.dto.DailyPracticeCountDto;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.MonthlyPracticeCountDto;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.WeeklyPracticeCountDto;
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

	public record YearWeek(int year, int week) {
		public static YearWeek from(LocalDate date) {
			WeekFields wf = WeekFields.ISO;
			return new YearWeek(date.get(wf.weekBasedYear()), date.get(wf.weekOfWeekBasedYear()));
		}
	}
}
