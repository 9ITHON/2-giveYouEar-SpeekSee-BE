package com._ithon.speeksee.domain.voicefeedback.streaming.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com._ithon.speeksee.domain.voicefeedback.streaming.entity.ScriptPractice;

public interface ScriptPracticeRepository extends JpaRepository<ScriptPractice, Long> {
	// This class can be extended to include custom query methods if needed
	// For example, you might want to find ScriptPractice by userId or scriptId
}
