package com._ithon.speeksee.domain.voicefeedback.statistics.repository;

import java.time.LocalDate;
import java.util.List;

import com.querydsl.core.Tuple;

public interface ScriptPracticeRepositoryCustom {

	List<Tuple> countDailyByMember(LocalDate startDate, LocalDate endDate, Long memberId);

	List<Tuple> countWeeklyByMember(LocalDate startDate, LocalDate endDate, Long memberId);

	List<Tuple> countMonthlyByMember(LocalDate startDate, LocalDate endDate, Long memberId);

}
