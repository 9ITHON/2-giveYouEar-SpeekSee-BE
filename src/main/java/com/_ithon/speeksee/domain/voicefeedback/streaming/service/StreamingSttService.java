package com._ithon.speeksee.domain.voicefeedback.streaming.service;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

import com._ithon.speeksee.domain.voicefeedback.streaming.port.StreamingSttClient;

import lombok.RequiredArgsConstructor;

/**
 * Streaming STT 서비스
 * <p>
 * 이 서비스는 WebSocket을 통해 음성 인식 클라이언트와 상호작용합니다.
 * - WebSocket 연결 시작, 오디오 메시지 수신, 연결 종료를 처리합니다.
 */
@Service
@RequiredArgsConstructor
public class StreamingSttService {

	private final StreamingSttClient streamingSttClient;

	public void handleStart(WebSocketSession session) {
		streamingSttClient.start(session);
	}

	public void handleAudio(WebSocketSession session, BinaryMessage message) {
		streamingSttClient.receiveAudio(session, message);
	}

	public void handleEnd(WebSocketSession session) {
		streamingSttClient.end(session);
	}
}
