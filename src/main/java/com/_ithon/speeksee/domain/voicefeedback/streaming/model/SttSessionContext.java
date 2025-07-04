package com._ithon.speeksee.domain.voicefeedback.streaming.model;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.web.socket.WebSocketSession;

import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.StreamController;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.StreamingRecognizeRequest;

import lombok.extern.slf4j.Slf4j;

/*
 * SttSessionContext.java
 *
 * 이 클래스는 STT(음성 인식) 세션의 컨텍스트를 관리합니다.
 * SpeechClient, 요청 스트림, 컨트롤러 및 WebSocket 세션을 포함합니다.
 *
 * 리소스 정리를 위한 closeResources 메서드를 제공합니다.
 */
@Slf4j
public class SttSessionContext {
	public SpeechClient client;
	public ClientStream<StreamingRecognizeRequest> requestStream;
	public StreamController controller;
	public WebSocketSession session;

	public AtomicInteger currentWordIndex = new AtomicInteger(0);

	public Long memberId; // 또는 memberId
	public Long scriptId;

	public void closeResources() {
		try {
			if (requestStream != null) requestStream.closeSend();
			if (client != null) client.close();
			if (session != null && session.isOpen()) session.close();
		} catch (Exception e) {
			log.warn("[{}] 리소스 종료 중 오류 발생", session.getId(), e);
		}
	}
}
