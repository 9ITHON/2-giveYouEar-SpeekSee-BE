package com._ithon.speeksee.domain.voicefeedback.statistics.repository;

import java.time.LocalDate;
import java.util.List;

import com._ithon.speeksee.domain.voicefeedback.statistics.dto.DailyAccuracyDto;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.MaxAccuracyTrendDto;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.MonthlyAccuracyDto;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.WeeklyAccuracyDto;
import com.querydsl.core.Tuple;

public interface ScriptPracticeRepositoryCustom {

	List<Tuple> countDailyByMember(LocalDate startDate, LocalDate endDate, Long memberId);

	List<Tuple> countWeeklyByMember(LocalDate startDate, LocalDate endDate, Long memberId);

	List<Tuple> countMonthlyByMember(LocalDate startDate, LocalDate endDate, Long memberId);

	List<DailyAccuracyDto> findDailyAccuracy(Long scriptId, LocalDate from, LocalDate to);

	List<WeeklyAccuracyDto> findWeeklyAccuracy(Long scriptId, LocalDate from, LocalDate to);

	List<MonthlyAccuracyDto> findMonthlyAccuracy(Long scriptId, LocalDate from, LocalDate to);

	List<MaxAccuracyTrendDto> findDailyMaxAccuracyByScript(Long scriptId);

}
