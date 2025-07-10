package com._ithon.speeksee.domain.member.service;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import com._ithon.speeksee.domain.member.entity.Level;
import com._ithon.speeksee.domain.member.entity.Member;
import com._ithon.speeksee.domain.member.repository.MemberRepository;
import com._ithon.speeksee.domain.script.domain.Script;
import com._ithon.speeksee.domain.script.repository.ScriptRepository;
import com._ithon.speeksee.domain.voicefeedback.streaming.infra.session.AuthenticatedSession;
import com._ithon.speeksee.domain.voicefeedback.streaming.infra.session.SttSessionContext;
import com._ithon.speeksee.domain.voicefeedback.streaming.infra.session.SttSessionManager;
import com._ithon.speeksee.global.infra.exception.entityException.ScriptNotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LevelTestService {

	private final MemberRepository memberRepository;

	@Transactional
	public void saveLevel(Long memberId, Level level) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id=" + memberId));

		member.setCurrentLevel(level);
		log.info("✅ 사용자 레벨 저장 완료: memberId={}, level={}", memberId, level);
	}
}