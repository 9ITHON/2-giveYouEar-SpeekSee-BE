package com._ithon.speeksee.domain.Script.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com._ithon.speeksee.domain.Script.domain.Script;

public interface ScriptRepository extends JpaRepository<Script, String> {
}
