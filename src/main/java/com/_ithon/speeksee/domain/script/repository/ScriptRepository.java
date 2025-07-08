package com._ithon.speeksee.domain.script.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com._ithon.speeksee.domain.script.domain.Script;

public interface ScriptRepository extends JpaRepository<Script, Long> {

	@Query("SELECT s FROM Script s WHERE s.isLevelTest = true")
	List<Script> findAllLevelTestScripts();
}
