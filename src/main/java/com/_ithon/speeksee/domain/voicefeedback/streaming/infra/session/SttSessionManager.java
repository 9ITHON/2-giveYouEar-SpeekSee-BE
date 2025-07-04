package com._ithon.speeksee.domain.voicefeedback.streaming.infra.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com._ithon.speeksee.domain.Script.repository.ScriptRepository;
import com._ithon.speeksee.domain.member.repository.MemberRepository;
import com._ithon.speeksee.domain.voicefeedback.streaming.model.SttSessionContext;
import com._ithon.speeksee.global.infra.exception.entityException.MemberNotFoundException;
import com._ithon.speeksee.global.infra.exception.entityException.ScriptNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SttSessionManager {

	private final MemberRepository memberRepository;
	private final ScriptRepository scriptRepository;

	private final Map<String, SttSessionContext> sessionMap = new ConcurrentHashMap<>();

	public SttSessionContext startSession(WebSocketSession session) {
		if (sessionMap.containsKey(session.getId())) {
			log.warn("[{}] 기존 세션 존재 → 삭제", session.getId());
			closeSession(session);
		}

		String memberIdStr = getQueryParam(session, "memberId");
		String scriptIdStr = getQueryParam(session, "scriptId");

		if (memberIdStr == null || scriptIdStr == null) {
			throw new IllegalArgumentException("memberId 또는 scriptId가 query에 없습니다.");
		}

		long memberId = Long.parseLong(memberIdStr);
		long scriptId = Long.parseLong(scriptIdStr);

		if (!memberRepository.existsById(memberId)) throw new MemberNotFoundException();
		if (!scriptRepository.existsById(scriptId)) throw new ScriptNotFoundException();

		SttSessionContext context = new SttSessionContext();
		context.session = session;
		context.memberId = memberId;
		context.scriptId = scriptId;

		sessionMap.put(session.getId(), context);
		return context;
	}

	public void closeSession(WebSocketSession session) {
		SttSessionContext context = sessionMap.remove(session.getId());
		if (context != null) {
			try {
				context.closeResources();
				log.info("[{}] 세션 종료 및 리소스 정리", session.getId());
			} catch (Exception e) {
				log.warn("[{}] 세션 종료 중 오류", session.getId(), e);
			}
		}
	}

	public SttSessionContext getContext(WebSocketSession session) {
		return sessionMap.get(session.getId());
	}

	private String getQueryParam(WebSocketSession session, String key) {
		String query = session.getUri().getQuery();
		if (query == null) return null;
		for (String param : query.split("&")) {
			String[] pair = param.split("=");
			if (pair.length == 2 && pair[0].equals(key)) {
				return pair[1];
			}
		}
		return null;
	}
}
