package com._ithon.speeksee.domain.voicefeedback.streaming.model;

import org.springframework.web.socket.WebSocketSession;

import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.StreamController;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.StreamingRecognizeRequest;

/*
 * SttSessionContext.java
 *
 * 이 클래스는 STT(음성 인식) 세션의 컨텍스트를 관리합니다.
 * SpeechClient, 요청 스트림, 컨트롤러 및 WebSocket 세션을 포함합니다.
 *
 * 리소스 정리를 위한 closeResources 메서드를 제공합니다.
 */
public class SttSessionContext {
	public SpeechClient client;
	public ClientStream<StreamingRecognizeRequest> requestStream;
	public StreamController controller;
	public WebSocketSession session;

	public void closeResources() {
		try {
			if (requestStream != null) {
				requestStream.closeSend();
			}
			if (client != null) {
				client.close();
			}
		} catch (Exception e) {
			// 로깅은 호출 쪽에서 처리
		}
	}
}
