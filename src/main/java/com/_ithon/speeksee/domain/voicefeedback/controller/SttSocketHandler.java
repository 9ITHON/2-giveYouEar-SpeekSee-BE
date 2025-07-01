package com._ithon.speeksee.domain.voicefeedback.controller;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import com._ithon.speeksee.domain.voicefeedback.port.StreamingSttClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SttSocketHandler extends BinaryWebSocketHandler {

	private final StreamingSttClient streamingSttClient;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		log.info("WebSocket 연결 시작: {}", session.getId());
		streamingSttClient.start(session);
	}

	@Override
	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
		log.debug("BinaryMessage 수신 from {} ({} bytes)", session.getId(), message.getPayloadLength());
		streamingSttClient.receiveAudio(session, message);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		log.info("WebSocket 연결 종료: {} ({})", session.getId(), status.getReason());
		streamingSttClient.end(session);
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		log.error("WebSocket 전송 에러: {}", session.getId(), exception.getMessage(), exception);
		try {
			streamingSttClient.end(session);
		} finally {
			if (session.isOpen()) {
				session.close(CloseStatus.SERVER_ERROR);
			}
		}
	}
}
