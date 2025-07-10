package com._ithon.speeksee.domain.script.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com._ithon.speeksee.domain.script.domain.Script;

public interface ScriptRepository extends JpaRepository<Script, Long>, ScriptRepositoryCustom {

	@Query(value = "SELECT * FROM script WHERE is_level_test = true ORDER BY RANDOM() LIMIT 3", nativeQuery = true)
	List<Script> find3ByIsLevelTestTrue();

	boolean existsByIsLevelTest(boolean isLevelTest);
}
