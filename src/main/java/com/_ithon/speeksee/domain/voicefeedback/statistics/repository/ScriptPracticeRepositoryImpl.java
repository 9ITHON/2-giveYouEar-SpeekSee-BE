package com._ithon.speeksee.domain.voicefeedback.statistics.repository;

import static com._ithon.speeksee.domain.script.domain.QScript.*;
import static com._ithon.speeksee.domain.voicefeedback.practice.entity.QScriptPractice.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com._ithon.speeksee.domain.script.domain.DifficultyLevel;
import com._ithon.speeksee.domain.voicefeedback.practice.entity.QScriptPractice;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.PracticeChartPoint;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.ScriptAccuracyDto;
import com._ithon.speeksee.domain.voicefeedback.statistics.dto.ScriptPracticeCountDto;
import com._ithon.speeksee.domain.voicefeedback.statistics.entity.PeriodType;
import com._ithon.speeksee.domain.voicefeedback.statistics.util.LabelConverter;
import com._ithon.speeksee.domain.voicefeedback.statistics.util.ScoreUtil;
import com.querydsl.core.Tuple;
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

	private final QScriptPractice sp = scriptPractice;

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

	@Override
	public List<ScriptAccuracyDto> findScriptAccuracyTrends(Long memberId, PeriodType periodType) {
		LocalDateTime startDate = periodType.startDate(LocalDate.now());

		List<Tuple> tuples = queryFactory
			.select(
				script.id,
				script.title,
				scriptPractice.accuracy,
				scriptPractice.createdAt
			)
			.from(scriptPractice)
			.join(scriptPractice.script, script)
			.where(
				scriptPractice.member.id.eq(memberId),
				scriptPractice.createdAt.goe(startDate)
			)
			.fetch();

		Map<Long, List<Tuple>> groupedByScript = tuples.stream()
			.collect(Collectors.groupingBy(t -> t.get(script.id)));

		List<ScriptAccuracyDto> result = new ArrayList<>();

		for (Map.Entry<Long, List<Tuple>> entry : groupedByScript.entrySet()) {
			Long scriptId = entry.getKey();
			String scriptTitle = entry.getValue().get(0).get(script.title);

			Map<String, List<Double>> accuracyByLabel = entry.getValue().stream()
				.collect(Collectors.groupingBy(
					t -> LabelConverter.convertToLabel(
						periodType,
						t.get(scriptPractice.createdAt).toLocalDate()
					),
					Collectors.mapping(t -> t.get(scriptPractice.accuracy), Collectors.toList())
				));

			List<PracticeChartPoint> points = accuracyByLabel.entrySet().stream()
				.map(e -> new PracticeChartPoint(
					e.getKey(),
					Math.round(e.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0))
				))
				.sorted(Comparator.comparing(PracticeChartPoint::label))
				.toList();

			result.add(new ScriptAccuracyDto(scriptId, scriptTitle, points));
		}

		return result;
	}

	@Override
	public List<ScriptPracticeCountDto> countScriptPracticeByPeriod(Long memberId, PeriodType periodType) {
		LocalDateTime startDate = periodType.startDate(LocalDate.now());

		// 날짜 문자열 추출용 (ex: 2025-07-01)
		Expression<String> dateExpr = Expressions.stringTemplate("to_char({0}, 'YYYY-MM-DD')",
			scriptPractice.createdAt);

		// scriptId, scriptTitle, date, count(*) 쿼리
		List<Tuple> tuples = queryFactory
			.select(
				script.id,
				script.title,
				dateExpr,
				scriptPractice.id.count()
			)
			.from(scriptPractice)
			.join(scriptPractice.script, script)
			.where(
				scriptPractice.member.id.eq(memberId),
				scriptPractice.createdAt.goe(startDate)
			)
			.groupBy(script.id, script.title, dateExpr)
			.fetch();

		// scriptId 기준으로 그룹핑
		Map<Long, List<Tuple>> groupedByScript = tuples.stream()
			.collect(Collectors.groupingBy(t -> t.get(script.id)));

		List<ScriptPracticeCountDto> result = new ArrayList<>();

		for (Map.Entry<Long, List<Tuple>> entry : groupedByScript.entrySet()) {
			Long scriptId = entry.getKey();
			List<Tuple> scriptTuples = entry.getValue();

			String scriptTitle = scriptTuples.get(0).get(script.title);

			// 날짜 -> count map 만들기
			Map<String, Long> dailyCounts = scriptTuples.stream()
				.collect(Collectors.toMap(
					t -> t.get(dateExpr),
					t -> t.get(scriptPractice.id.count()),
					Long::sum
				));

			// 날짜 오름차순 정렬 후 누적 합 계산
			List<PracticeChartPoint> points = new ArrayList<>();
			long cumulative = 0;

			for (String date : dailyCounts.keySet().stream().sorted().toList()) {
				cumulative += dailyCounts.get(date);
				points.add(new PracticeChartPoint(date, cumulative));
			}

			result.add(new ScriptPracticeCountDto(scriptId, scriptTitle, points));
		}

		return result;
	}

	@Override
	public List<PracticeChartPoint> calculateTotalScoreOverTime(Long memberId, PeriodType periodType) {
		LocalDateTime startDate = periodType.startDate(LocalDate.now());

		// 쿼리: scriptId, difficulty, MAX(accuracy), MIN(createdAt)
		List<Tuple> results = queryFactory
			.select(
				script.id,
				script.difficultyLevel,
				scriptPractice.accuracy.max(),
				scriptPractice.createdAt.min()
			)
			.from(scriptPractice)
			.join(scriptPractice.script, script)
			.where(
				scriptPractice.member.id.eq(memberId),
				scriptPractice.createdAt.goe(startDate)
			)
			.groupBy(script.id, script.difficultyLevel)
			.fetch();

		// Map<String date, Long score>
		Map<String, Long> scoreByDate = new HashMap<>();

		for (Tuple tuple : results) {
			DifficultyLevel level = tuple.get(script.difficultyLevel);
			int difficultyScore = ScoreUtil.difficultyToScore(level);

			double maxAccuracy = tuple.get(scriptPractice.accuracy.max());
			long score = Math.round(maxAccuracy * difficultyScore);

			LocalDate date = tuple.get(scriptPractice.createdAt.min()).toLocalDate();
			String label = date.toString();

			scoreByDate.merge(label, score, Long::sum);
		}

		// 누적 합산
		List<PracticeChartPoint> result = new ArrayList<>();
		long cumulative = 0;

		for (String date : scoreByDate.keySet().stream().sorted().toList()) {
			cumulative += scoreByDate.get(date);
			result.add(new PracticeChartPoint(date, cumulative));
		}

		return result;
	}
}


