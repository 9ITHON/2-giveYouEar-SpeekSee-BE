package com._ithon.speeksee.domain.voicefeedback.streaming.controller;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import com._ithon.speeksee.domain.voicefeedback.streaming.service.StreamingSttService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * WebSocket을 통한 음성 인식 핸들러
 * <p>
 * 이 핸들러는 WebSocket 연결을 관리하고, 오디오 메시지를 처리하며, 연결 종료 시 클린업 작업을 수행합니다.
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class SttSocketHandler extends BinaryWebSocketHandler {

	private final StreamingSttService sttService;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		log.info("WebSocket 연결 시작: {}", session.getId());
		sttService.handleStart(session);
	}

	@Override
	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
		log.debug("BinaryMessage 수신: {}", session.getId());
		sttService.handleAudio(session, message);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		log.info("WebSocket 연결 종료: {}", session.getId());
		sttService.handleEnd(session);
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) {
		log.error("WebSocket 오류: {}", session.getId(), exception);
		sttService.handleEnd(session);
	}
}
