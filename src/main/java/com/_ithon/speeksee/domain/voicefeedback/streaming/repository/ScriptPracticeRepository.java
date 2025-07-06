package com._ithon.speeksee.domain.voicefeedback.streaming.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com._ithon.speeksee.domain.member.entity.Member;
import com._ithon.speeksee.domain.voicefeedback.streaming.entity.ScriptPractice;

public interface ScriptPracticeRepository extends JpaRepository<ScriptPractice, Long> {
	List<ScriptPractice> findAllByMember(Member member);

}
