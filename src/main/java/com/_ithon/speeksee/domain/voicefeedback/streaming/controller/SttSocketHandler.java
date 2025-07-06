package com._ithon.speeksee.domain.voicefeedback.streaming.controller;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import com._ithon.speeksee.domain.voicefeedback.streaming.infra.session.AuthenticatedSession;
import com._ithon.speeksee.domain.voicefeedback.streaming.service.StreamingSttService;
import com._ithon.speeksee.global.auth.jwt.JwtTokenProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
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
	private final JwtTokenProvider jwtTokenProvider; // JWT 토큰 검증을 위한 컴포넌트
	private final ObjectMapper objectMapper; // JSON 파싱을 위한 ObjectMapper

	@PostConstruct
	public void init() {
		log.info("🔌 WebSocket 핸들러 로딩됨");
	}


	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		log.info("WebSocket 연결 시작: {}", session.getId());
	}

	@Override
	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
		AuthenticatedSession auth = (AuthenticatedSession) session.getAttributes().get("auth");
		if (auth == null) {
			log.warn("인증되지 않은 세션의 바이너리 메시지: {}", session.getId());
			try {
				session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Unauthorized"));
			} catch (IOException e) {
				log.error("세션 종료 실패", e);
			}
			return;
		}

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

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {
		try {
			String payload = message.getPayload();
			JsonNode json = objectMapper.readTree(payload);

			String type = json.get("type").asText();
			if (!"AUTH".equals(type)) {
				log.warn("[{}] 알 수 없는 메시지 타입: {}", session.getId(), type);
				session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Invalid message type"));
				return;
			}

			String token = json.get("token").asText().replace("Bearer ", "").trim();
			Long memberId = json.get("memberId").asLong();
			Long scriptId = json.get("scriptId").asLong();

			jwtTokenProvider.validateToken(token);
			String email = jwtTokenProvider.getEmailFromToken(token);

			AuthenticatedSession auth = new AuthenticatedSession(memberId, email, scriptId);
			session.getAttributes().put("auth", auth);

			log.info("✅ WebSocket 인증 성공: sessionId={}, memberId={}, email={}", session.getId(), memberId, email);
			sttService.handleStart(session);
			session.sendMessage(new TextMessage("{\"type\":\"AUTH_OK\",\"message\":\"인증 완료\"}"));

		} catch (Exception e) {
			log.warn("❌ WebSocket 인증 실패: {}", e.getMessage());
			try {
				session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Invalid AUTH"));
			} catch (IOException ex) {
				log.error("세션 종료 실패", ex);
			}
		}
	}
}
