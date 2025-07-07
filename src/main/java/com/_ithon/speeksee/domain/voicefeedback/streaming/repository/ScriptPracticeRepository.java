package com._ithon.speeksee.domain.voicefeedback.streaming.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com._ithon.speeksee.domain.member.entity.Member;
import com._ithon.speeksee.domain.voicefeedback.streaming.entity.ScriptPractice;

public interface ScriptPracticeRepository extends JpaRepository<ScriptPractice, Long> {
	List<ScriptPractice> findAllByMember(Member member);

	@Query(value = """
	SELECT CAST(created_at AS DATE) AS date, COUNT(DISTINCT script_id) AS count
	FROM script_practice
	WHERE member_id = :memberId
	  AND created_at >= CURRENT_DATE - INTERVAL '6 days'
	GROUP BY CAST(created_at AS DATE)
	ORDER BY date ASC
""", nativeQuery = true)
	List<Object[]> countDailyPracticeLastWeek(@Param("memberId") Long memberId);

	@Query(value = """
	SELECT TO_CHAR(created_at, 'IYYY-IW') AS week, COUNT(DISTINCT script_id) AS count
	FROM script_practice
	WHERE member_id = :memberId
	  AND created_at >= CURRENT_DATE - INTERVAL '6 months'
	GROUP BY week
	ORDER BY week ASC
""", nativeQuery = true)
	List<Object[]> countWeeklyPracticeLastSixMonths(@Param("memberId") Long memberId);


	@Query(value = """
	SELECT TO_CHAR(created_at, 'YYYY-MM') AS month, COUNT(DISTINCT script_id) AS count
	FROM script_practice
	WHERE member_id = :memberId
	  AND created_at >= CURRENT_DATE - INTERVAL '1 year'
	GROUP BY month
	ORDER BY month ASC
""", nativeQuery = true)
	List<Object[]> countMonthlyPracticeLastYear(@Param("memberId") Long memberId);


}
