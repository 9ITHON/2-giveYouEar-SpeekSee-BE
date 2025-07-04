package com._ithon.speeksee.domain.voicefeedback.streaming.infra.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com._ithon.speeksee.domain.voicefeedback.streaming.model.SttSessionContext;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SttSessionManager {

	private final Map<String, SttSessionContext> sessionMap = new ConcurrentHashMap<>();

	public SttSessionContext startSession(WebSocketSession session) {
		String sessionId = session.getId();

		// 기존 세션이 존재하면 종료
		if (sessionMap.containsKey(sessionId)) {
			log.warn("[{}] 기존 세션 존재. 종료 후 새 세션 시작", sessionId);
			closeSession(session);
		}

		SttSessionContext context = new SttSessionContext();
		context.session = session;
		sessionMap.put(sessionId, context);

		return context;
	}

	public SttSessionContext getSession(String sessionId) {
		return sessionMap.get(sessionId);
	}

	public void closeSession(WebSocketSession session) {
		String sessionId = session.getId();
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

	@Scheduled(fixedDelay = 60000) // 1분마다 실행
	public void cleanupExpiredSessions() {
		for (Map.Entry<String, SttSessionContext> entry : sessionMap.entrySet()) {
			String sessionId = entry.getKey();
			SttSessionContext context = entry.getValue();

			if (context.isExpired()) {
				log.warn("[{}] 세션 TTL 만료. 자동 정리", sessionId);
				closeSession(context.session);
			}
		}
	}
}