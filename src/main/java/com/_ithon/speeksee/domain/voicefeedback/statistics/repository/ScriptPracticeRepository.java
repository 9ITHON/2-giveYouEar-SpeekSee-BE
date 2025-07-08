package com._ithon.speeksee.domain.voicefeedback.statistics.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com._ithon.speeksee.domain.member.entity.Member;
import com._ithon.speeksee.domain.voicefeedback.practice.entity.ScriptPractice;

public interface ScriptPracticeRepository extends JpaRepository<ScriptPractice, Long>, ScriptPracticeRepositoryCustom {

	List<ScriptPractice> findAllByMember(Member member);

	@Query("""
	SELECT COUNT(DISTINCT sp.script.id)
	FROM ScriptPractice sp
	WHERE sp.member.id = :memberId
	  AND sp.createdAt < :before
""")
	long countDistinctScriptsBeforeDate(
		@Param("memberId") Long memberId,
		@Param("before") LocalDateTime before
	);

}
