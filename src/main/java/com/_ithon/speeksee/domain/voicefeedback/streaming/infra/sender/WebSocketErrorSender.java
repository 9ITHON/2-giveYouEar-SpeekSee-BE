package com._ithon.speeksee.domain.voicefeedback.streaming.infra.sender;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class WebSocketErrorSender {

	public void sendErrorAndClose(WebSocketSession session, String code, String message) {
		if (session == null || !session.isOpen()) return;

		try {
			String json = String.format("""
			{
			  "type": "ERROR",
			  "code": "%s",
			  "message": "%s"
			}
			""", code, message);

			session.sendMessage(new TextMessage(json));
			log.warn("[{}] 에러 전송: {} - {}", session.getId(), code, message);

			session.close();

		} catch (Exception e) {
			log.error("[{}] 에러 메시지 전송 실패", session.getId(), e);
		}
	}
}