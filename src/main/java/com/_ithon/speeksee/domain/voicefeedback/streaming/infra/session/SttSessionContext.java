package com._ithon.speeksee.domain.voicefeedback.streaming.infra.session;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.web.socket.WebSocketSession;

import com._ithon.speeksee.domain.voicefeedback.streaming.dto.response.WordInfoDto;
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
	public String mode = "normal"; // 레벨 테스트 모드 여부

	public List<WordInfoDto> levelTestWordInfos = new ArrayList<>(); // 레벨 테스트 시 누적된 단어 일치 정보
	public boolean levelTestProcessed = false;

	public AtomicInteger currentWordIndex = new AtomicInteger(0);

	public Long memberId; // 또는 memberId
	public Long scriptId;

	public long createdAt = System.currentTimeMillis();  // 생성 시점 기록
	public long ttlMillis = 5 * 60 * 1000; // 5분 TTL

	public boolean isExpired() {
		return System.currentTimeMillis() > createdAt + ttlMillis;
	}

	public void closeResources() {
		try {
			if (requestStream != null)
				requestStream.closeSend(); // 요청 스트림만 닫는다
			if (session != null && session.isOpen())
				session.close(); // WebSocket만 닫는다
		} catch (Exception e) {
			log.warn("[{}] 리소스 종료 중 오류 발생", session != null ? session.getId() : "UNKNOWN", e);
		}
	}
}
