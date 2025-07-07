package com._ithon.speeksee.domain.voicefeedback.statistics.repository;

import static com._ithon.speeksee.domain.voicefeedback.practice.entity.QScriptPractice.*;
import static com.querydsl.core.types.Order.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import com._ithon.speeksee.domain.voicefeedback.practice.entity.QScriptPractice;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.DailyAccuracyDto;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.MaxAccuracyTrendDto;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.MonthlyAccuracyDto;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.WeeklyAccuracyDto;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ScriptPracticeRepositoryImpl implements ScriptPracticeRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	private final QScriptPractice sp = scriptPractice;



	@Override
	public List<Tuple> countDailyByMember(LocalDate startDate, LocalDate endDate, Long memberId) {
		Expression<LocalDate> truncatedDate = Expressions.dateTemplate(
			LocalDate.class, "date({0})", sp.createdAt
		);

		return queryFactory
			.select(truncatedDate, sp.count())
			.from(sp)
			.where(
				sp.member.id.eq(memberId),
				sp.createdAt.between(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX))
			)
			.groupBy(truncatedDate)
			.orderBy(new OrderSpecifier<>(ASC, truncatedDate))
			.fetch();
	}

	@Override
	public List<Tuple> countWeeklyByMember(LocalDate startDate, LocalDate endDate, Long memberId) {
		NumberTemplate<Integer> yearExpr = Expressions.numberTemplate(Integer.class, "year({0})", sp.createdAt);
		NumberTemplate<Integer> weekExpr = Expressions.numberTemplate(Integer.class, "week({0})", sp.createdAt);

		return queryFactory
			.select(yearExpr, weekExpr, sp.count())
			.from(sp)
			.where(
				sp.member.id.eq(memberId),
				sp.createdAt.between(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX))
			)
			.groupBy(yearExpr, weekExpr)
			.orderBy(
				new OrderSpecifier<>(Order.ASC, yearExpr),
				new OrderSpecifier<>(Order.ASC, weekExpr)
			)
			.fetch();
	}

	@Override
	public List<Tuple> countMonthlyByMember(LocalDate startDate, LocalDate endDate, Long memberId) {
		NumberTemplate<Integer> yearExpr = Expressions.numberTemplate(Integer.class, "year({0})", sp.createdAt);
		NumberTemplate<Integer> monthExpr = Expressions.numberTemplate(Integer.class, "month({0})", sp.createdAt);

		return queryFactory
			.select(yearExpr, monthExpr, sp.count())
			.from(sp)
			.where(
				sp.member.id.eq(memberId),
				sp.createdAt.between(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX))
			)
			.groupBy(yearExpr, monthExpr)
			.orderBy(
				new OrderSpecifier<>(Order.ASC, yearExpr),
				new OrderSpecifier<>(Order.ASC, monthExpr)
			)
			.fetch();
	}

	@Override
	public List<DailyAccuracyDto> findDailyAccuracy(Long scriptId, LocalDate from, LocalDate to) {
		Expression<java.sql.Date> truncatedDate = Expressions.dateTemplate(
			java.sql.Date.class, "date({0})", sp.createdAt
		);
		NumberExpression<Double> avgAccuracy = sp.accuracy.avg();

		return queryFactory
			.select(truncatedDate, avgAccuracy)
			.from(sp)
			.where(
				sp.script.id.eq(scriptId),
				sp.createdAt.between(from.atStartOfDay(), to.atTime(LocalTime.MAX))
			)
			.groupBy(truncatedDate)
			.orderBy(new OrderSpecifier<>(Order.ASC, truncatedDate))
			.fetch()
			.stream()
			.map(t -> new DailyAccuracyDto(
				t.get(truncatedDate).toLocalDate(),
				t.get(avgAccuracy)
			))
			.toList();
	}

	@Override
	public List<WeeklyAccuracyDto> findWeeklyAccuracy(Long scriptId, LocalDate from, LocalDate to) {
		Expression<java.sql.Date> weekStartDate = Expressions.dateTemplate(
			java.sql.Date.class, "cast(date_trunc('week', {0}) as date)", sp.createdAt
		);
		NumberExpression<Double> avgAccuracy = sp.accuracy.avg();

		return queryFactory
			.select(weekStartDate, avgAccuracy)
			.from(sp)
			.where(
				sp.script.id.eq(scriptId),
				sp.createdAt.between(from.atStartOfDay(), to.atTime(LocalTime.MAX))
			)
			.groupBy(weekStartDate)
			.orderBy(new OrderSpecifier<>(Order.ASC, weekStartDate))
			.fetch()
			.stream()
			.map(t -> new WeeklyAccuracyDto(
				t.get(weekStartDate).toLocalDate(),
				t.get(avgAccuracy)
			))
			.toList();
	}

	@Override
	public List<MonthlyAccuracyDto> findMonthlyAccuracy(Long scriptId, LocalDate from, LocalDate to) {
		Expression<java.sql.Date> monthStartDate = Expressions.dateTemplate(
			java.sql.Date.class, "cast(date_trunc('month', {0}) as date)", sp.createdAt
		);
		NumberExpression<Double> avgAccuracy = sp.accuracy.avg();

		return queryFactory
			.select(monthStartDate, avgAccuracy)
			.from(sp)
			.where(
				sp.script.id.eq(scriptId),
				sp.createdAt.between(from.atStartOfDay(), to.atTime(LocalTime.MAX))
			)
			.groupBy(monthStartDate)
			.orderBy(new OrderSpecifier<>(Order.ASC, monthStartDate))
			.fetch()
			.stream()
			.map(t -> new MonthlyAccuracyDto(
				YearMonth.from(t.get(monthStartDate).toLocalDate()),
				t.get(avgAccuracy)
			))
			.toList();
	}

	@Override
	public List<MaxAccuracyTrendDto> findDailyMaxAccuracyByScript(Long scriptId) {
		// 1. 날짜별 max(accuracy) 조회
		Expression<java.sql.Date> truncatedDate = Expressions.dateTemplate(
			java.sql.Date.class, "cast({0} as date)", sp.createdAt
		);

		NumberExpression<Double> maxAccuracy = sp.accuracy.max();

		List<Tuple> rawMaxList = queryFactory
			.select(truncatedDate, maxAccuracy)
			.from(sp)
			.where(sp.script.id.eq(scriptId))
			.groupBy(truncatedDate)
			.orderBy(new OrderSpecifier<>(Order.ASC, truncatedDate))
			.fetch();

		// 2. 누적 max 처리
		List<MaxAccuracyTrendDto> result = new ArrayList<>();
		double maxSoFar = 0.0;

		for (Tuple tuple : rawMaxList) {
			LocalDate date = tuple.get(truncatedDate).toLocalDate();  // ✅ 안전 변환
			Double dailyMax = tuple.get(maxAccuracy);
			if (dailyMax != null) {
				maxSoFar = Math.max(maxSoFar, dailyMax);
			}
			result.add(new MaxAccuracyTrendDto(date, maxSoFar));
		}

		return result;
	}

}