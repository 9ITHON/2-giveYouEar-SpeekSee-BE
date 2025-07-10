package com._ithon.speeksee.domain.script.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com._ithon.speeksee.domain.member.entity.Member;
import com._ithon.speeksee.domain.script.domain.DifficultyLevel;
import com._ithon.speeksee.domain.script.domain.QScript;
import com._ithon.speeksee.domain.script.domain.Script;
import com._ithon.speeksee.domain.script.domain.ScriptSortOption;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ScriptRepositoryImpl implements ScriptRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<Script> findByAuthorWithSort(Member author, ScriptSortOption sortOption) {
		QScript script = QScript.script;

		JPQLQuery<Script> query = queryFactory.selectFrom(script)
			.where(script.author.eq(author));

		// 정렬 조건 분기
		switch (sortOption) {
			case CREATED_ASC -> query.orderBy(script.createdAt.asc());
			case CREATED_DESC -> query.orderBy(script.createdAt.desc());
			case COUNT_DESC -> query.orderBy(script.practiceCount.desc());
			case COUNT_ASC -> query.orderBy(script.practiceCount.asc());
			case TITLE_ASC -> query.orderBy(script.title.asc());
			case TITLE_DESC -> query.orderBy(script.title.desc());
			case DIFFICULTY_ASC -> query.orderBy(difficultyOrder(script).asc());
			case DIFFICULTY_DESC -> query.orderBy(difficultyOrder(script).desc());
			case UPDATED_ASC -> query.orderBy(script.updatedAt.asc());
			case UPDATED_DESC -> query.orderBy(script.updatedAt.desc());
			default -> query.orderBy(script.createdAt.desc()); // fallback
		}

		return query.fetch();
	}

	private NumberExpression<Integer> difficultyOrder(QScript script) {
		return new CaseBuilder()
			.when(script.difficultyLevel.eq(DifficultyLevel.EASY)).then(1)
			.when(script.difficultyLevel.eq(DifficultyLevel.MEDIUM)).then(2)
			.when(script.difficultyLevel.eq(DifficultyLevel.HARD)).then(3)
			.otherwise(99);
	}
}


