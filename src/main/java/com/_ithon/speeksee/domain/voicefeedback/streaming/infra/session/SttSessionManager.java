package com._ithon.speeksee.domain.voicefeedback.streaming.infra.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SttSessionManager {

	private final Map<String, SttSessionContext> sessionMap = new ConcurrentHashMap<>();

	public SttSessionContext startSession(WebSocketSession session) {
		if (sessionMap.containsKey(session.getId())) {
			log.warn("[{}] 이미 활성화된 세션입니다.", session.getId());
			return sessionMap.get(session.getId());
		}

		SttSessionContext context = new SttSessionContext();
		context.session = session;

		// 세션에서 인증 정보 꺼내기
		Object authAttr = session.getAttributes().get("auth");
		if (authAttr instanceof AuthenticatedSession auth) {
			context.memberId = auth.getMemberId();
			context.scriptId = auth.getScriptId();
			// 필요 시 context.email = auth.getEmail(); 추가 가능
			log.info("인증 정보 적용됨: memberId={}, scriptId={}", auth.getMemberId(), auth.getScriptId());
		} else {
			log.warn("[{}] 인증 정보가 존재하지 않습니다. AUTH 메시지를 먼저 보내야 합니다.", session.getId());
		}

		sessionMap.put(session.getId(), context);
		return context;
	}

	public SttSessionContext getSession(String sessionId) {
		return sessionMap.get(sessionId);
	}
	/**
	 * 세션을 종료하고 리소스를 정리합니다.
	 * @param sessionId 세션 ID
	 */
	public void closeSession(String sessionId) {
		SttSessionContext context = sessionMap.remove(sessionId);
		if (context != null) {
			try {
				context.closeResources();
				log.info("[{}] 세션 리소스 정리 완료", sessionId);
			} catch (Exception e) {
				log.warn("[{}] 세션 종료 중 오류", sessionId, e);
			}
		}
	}

	/**
	 * WebSocketSession을 통해 세션을 종료합니다.
	 * @param session WebSocketSession
	 */
	// 기존 WebSocketSession 기반 메서드는 위 메서드를 재사용
	public void closeSession(WebSocketSession session) {
		closeSession(session.getId());
	}

	/**
	 * 주기적으로 모든 세션을 종료하고 리소스를 정리합니다.
	 */
	@Scheduled(fixedDelay = 60000) // 1분마다 실행
	public void cleanupExpiredSessions() {
		for (Map.Entry<String, SttSessionContext> entry : sessionMap.entrySet()) {
			String sessionId = entry.getKey();
			SttSessionContext context = entry.getValue();

			if (context != null && context.isExpired()) {
				log.warn("[{}] 세션 TTL 만료. 자동 정리", sessionId);
				closeSession(sessionId);
			}
		}
	}
}
