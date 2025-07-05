package com._ithon.speeksee.domain.voicefeedback.streaming.port;

import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

public interface StreamingSttClient {

	void start(WebSocketSession session);

	void receiveAudio(WebSocketSession session, BinaryMessage message);

	void end(WebSocketSession session);
}
