package com._ithon.speeksee.domain.voicefeedback.statistics.repository;

import java.util.List;

import com._ithon.speeksee.domain.voicefeedback.statistics.dto.PracticeChartPoint;

public interface ScriptPracticeRepositoryCustom {
	List<PracticeChartPoint> countDailyPracticeLastWeek(Long memberId);
	List<PracticeChartPoint> countWeeklyPracticeLastSixMonths(Long memberId);
	List<PracticeChartPoint> countMonthlyPracticeLastYear(Long memberId);
}
