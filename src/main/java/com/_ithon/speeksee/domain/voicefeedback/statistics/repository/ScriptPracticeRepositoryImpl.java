package com._ithon.speeksee.domain.voicefeedback.statistics.repository;

import java.time.LocalDate;
import java.util.List;

import com._ithon.speeksee.domain.voicefeedback.practice.entity.QScriptPractice;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.PracticeChartPoint;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ScriptPracticeRepositoryImpl implements ScriptPracticeRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	private final QScriptPractice sp = QScriptPractice.scriptPractice;

	private OrderSpecifier<?> asc(Expression<String> expr) {
		return new OrderSpecifier<>(Order.ASC, expr);
	}

	@Override
	public List<PracticeChartPoint> countDailyPracticeLastWeek(Long memberId) {
		LocalDate today = LocalDate.now();
		LocalDate start = today.minusDays(6);

		Expression<String> dateExpr = Expressions.stringTemplate("cast({0} as date)", sp.createdAt);

		return queryFactory
			.select(Projections.constructor(
				PracticeChartPoint.class,
				dateExpr,
				sp.script.id.countDistinct()
			))
			.from(sp)
			.where(
				sp.member.id.eq(memberId),
				sp.createdAt.goe(start.atStartOfDay())
			)
			.groupBy(dateExpr)
			.orderBy(asc(dateExpr))
			.fetch();
	}

	@Override
	public List<PracticeChartPoint> countWeeklyPracticeLastSixMonths(Long memberId) {
		LocalDate start = LocalDate.now().minusMonths(6);

		Expression<String> weekExpr = Expressions.stringTemplate(
			"to_char({0}, {1})", sp.createdAt, ConstantImpl.create("IYYY-IW"));

		return queryFactory
			.select(Projections.constructor(
				PracticeChartPoint.class,
				weekExpr,
				sp.script.id.countDistinct()
			))
			.from(sp)
			.where(
				sp.member.id.eq(memberId),
				sp.createdAt.goe(start.atStartOfDay())
			)
			.groupBy(weekExpr)
			.orderBy(asc(weekExpr))
			.fetch();
	}

	@Override
	public List<PracticeChartPoint> countMonthlyPracticeLastYear(Long memberId) {
		LocalDate start = LocalDate.now().minusYears(1);

		Expression<String> monthExpr = Expressions.stringTemplate(
			"to_char({0}, {1})", sp.createdAt, ConstantImpl.create("YYYY-MM"));

		return queryFactory
			.select(Projections.constructor(
				PracticeChartPoint.class,
				monthExpr,
				sp.script.id.countDistinct()
			))
			.from(sp)
			.where(
				sp.member.id.eq(memberId),
				sp.createdAt.goe(start.atStartOfDay())
			)
			.groupBy(monthExpr)
			.orderBy(asc(monthExpr))
			.fetch();
	}
}

