package com._ithon.speeksee.domain.voicefeedback.statistics.repository;

import java.util.List;

import com._ithon.speeksee.domain.voicefeedback.statistics.dto.PracticeChartPoint;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.ScriptAccuracyDto;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.ScriptPracticeCountDto;
import com._ithon.speeksee.domain.voicefeedback.statistics.entity.PeriodType;

public interface ScriptPracticeRepositoryCustom {
	List<PracticeChartPoint> countDailyPracticeLastWeek(Long memberId);
	List<PracticeChartPoint> countWeeklyPracticeLastSixMonths(Long memberId);
	List<PracticeChartPoint> countMonthlyPracticeLastYear(Long memberId);
	List<ScriptAccuracyDto> findScriptAccuracyTrends(Long memberId, PeriodType periodType);
	List<ScriptPracticeCountDto> countScriptPracticeByPeriod(Long memberId, PeriodType periodType);

}
