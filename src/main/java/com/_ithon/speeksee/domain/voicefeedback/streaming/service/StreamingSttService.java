package com._ithon.speeksee.domain.voicefeedback.streaming.service;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

import com._ithon.speeksee.domain.voicefeedback.streaming.infra.response.GoogleSttResponseObserver;
import com._ithon.speeksee.domain.voicefeedback.streaming.infra.session.SttSessionContext;
import com._ithon.speeksee.domain.voicefeedback.streaming.infra.session.SttSessionManager;
import com._ithon.speeksee.domain.voicefeedback.streaming.port.StreamingSttClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Streaming STT 서비스
 * <p>
 * 이 서비스는 WebSocket을 통해 음성 인식 클라이언트와 상호작용합니다.
 * - WebSocket 연결 시작, 오디오 메시지 수신, 연결 종료를 처리합니다.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StreamingSttService {

	private final StreamingSttClient streamingSttClient;
	private final SttSessionManager sessionManager;

	public void handleStart(WebSocketSession session) {
		streamingSttClient.start(session);
	}

	public void handleAudio(WebSocketSession session, BinaryMessage message) {
		streamingSttClient.receiveAudio(session, message);
	}

	public void handleEnd(WebSocketSession session) {
		streamingSttClient.end(session);
	}

	public void flushCurrentSentence(WebSocketSession session) {
		log.info("[{}] flushCurrentSentence() 호출됨", session.getId());

		SttSessionContext context = sessionManager.getSession(session.getId());
		if (context == null) {
			log.warn("[{}] 세션 없음 → flush 스킵", session.getId());
			return;
		}

		GoogleSttResponseObserver observer = context.getObserver();
		if (observer == null) {
			log.warn("[{}] observer 없음 → flush 스킵", session.getId());
			return;
		}

		observer.flushAccumulatedResults();
	}
}
