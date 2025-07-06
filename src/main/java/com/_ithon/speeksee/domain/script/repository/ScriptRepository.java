package com._ithon.speeksee.domain.script.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com._ithon.speeksee.domain.script.domain.Script;

public interface ScriptRepository extends JpaRepository<Script, Long> {
}
