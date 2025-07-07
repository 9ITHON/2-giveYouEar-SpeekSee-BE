package com._ithon.speeksee.domain.voicefeedback.statistics.repository;

import static com._ithon.speeksee.domain.voicefeedback.practice.entity.QScriptPractice.*;
import static com.querydsl.core.types.Order.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com._ithon.speeksee.domain.voicefeedback.practice.entity.QScriptPractice;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
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


}


