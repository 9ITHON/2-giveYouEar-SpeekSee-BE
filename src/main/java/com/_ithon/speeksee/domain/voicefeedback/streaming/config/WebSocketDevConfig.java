package com._ithon.speeksee.domain.voicefeedback.streaming.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com._ithon.speeksee.domain.voicefeedback.streaming.controller.SttSocketHandler;

import lombok.RequiredArgsConstructor;

/**
 * 프로덕션 환경에서의 WebSocket 설정
 * - 특정 도메인에서만 WebSocket 연결을 허용합니다.
 */
@Profile("dev")
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketDevConfig implements WebSocketConfigurer {

	private final SttSocketHandler sttSocketHandler;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry
			.addHandler(sttSocketHandler, "/ws/stt")
			.setAllowedOrigins("*"); // 모든 출처 허용
	}
}

