package com._ithon.speeksee.domain.voicefeedback.streaming.infra;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com._ithon.speeksee.domain.voicefeedback.streaming.dto.response.TranscriptResult;
import com._ithon.speeksee.domain.voicefeedback.streaming.port.StreamingSttClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.StreamingRecognitionConfig;
import com.google.cloud.speech.v1.StreamingRecognitionResult;
import com.google.cloud.speech.v1.StreamingRecognizeRequest;
import com.google.cloud.speech.v1.StreamingRecognizeResponse;
import com.google.protobuf.ByteString;

import lombok.extern.slf4j.Slf4j;

/**
 * Google Cloud Speech-to-Text 스트리밍 클라이언트 구현
 * <p>
 * 이 클래스는 Google Cloud Speech-to-Text API를 사용하여 WebSocket을 통해 실시간 음성 인식을 처리합니다.
 */
@Component
@Slf4j
public class GoogleStreamingSttClient implements StreamingSttClient {

	private static final int SAMPLE_RATE = 16000;

	private final SpeechClient speechClient;
	private final ObjectMapper objectMapper = new ObjectMapper();

	// 세션 ID → 요청 스트림
	private final Map<String, ClientStream<StreamingRecognizeRequest>> streamMap = new ConcurrentHashMap<>();

	// 생성자에서 단 1번만 SpeechClient 생성
	public GoogleStreamingSttClient() throws IOException {
		this.speechClient = SpeechClient.create();
	}

	/**
	 * WebSocket 세션이 시작될 때 호출됩니다.
	 * <p>
	 * STT 스트리밍을 시작하고 초기 설정 요청을 전송합니다.
	 *
	 * @param session WebSocket 세션
	 */
	@Override
	public void start(WebSocketSession session) {
		try {
			ResponseObserver<StreamingRecognizeResponse> responseObserver = new ResponseObserver<>() {
				@Override
				public void onStart(StreamController controller) {
					log.info("🎙STT 스트리밍 시작: {}", session.getId());
				}

				@Override
				public void onResponse(StreamingRecognizeResponse response) {
					for (StreamingRecognitionResult result : response.getResultsList()) {
						if (!result.getIsFinal()) {
							return; // interim result는 무시
						}

						if (result.getAlternativesCount() > 0) {
							String transcript = result.getAlternatives(0).getTranscript();
							float confidence = result.getAlternatives(0).getConfidence();

							TranscriptResult dto = TranscriptResult.builder()
								.transcript(transcript)
								.confidence(confidence)
								.isFinal(true)
								.build();

							try {
								String json = objectMapper.writeValueAsString(dto);
								session.sendMessage(new TextMessage(json));
							} catch (IOException e) {
								log.error("[{}] WebSocket 응답 전송 실패", session.getId(), e);
							}
						}
					}
				}

				@Override
				public void onComplete() {
					log.info("STT 스트리밍 완료: {}", session.getId());
				}

				@Override
				public void onError(Throwable t) {
					log.error("STT 스트리밍 오류: {}", session.getId(), t);
				}
			};

			ClientStream<StreamingRecognizeRequest> clientStream =
				speechClient.streamingRecognizeCallable().splitCall(responseObserver);

			// 초기 설정 요청 전송
			clientStream.send(StreamingRecognizeRequest.newBuilder()
				.setStreamingConfig(StreamingRecognitionConfig.newBuilder()
					.setConfig(RecognitionConfig.newBuilder()
						.setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
						.setLanguageCode("ko-KR")
						.setSampleRateHertz(SAMPLE_RATE)
						.build())
					.setInterimResults(true)
					.setSingleUtterance(false)
					.build())
				.build());

			streamMap.put(session.getId(), clientStream);

		} catch (Exception e) {
			log.error("STT 스트리밍 세션 생성 실패: {}", session.getId(), e);
		}
	}

	/**
	 * WebSocket 세션에서 오디오 데이터를 수신할 때 호출됩니다.
	 * <p>
	 * 수신된 오디오 데이터를 STT 스트리밍 요청으로 전송합니다.
	 *
	 * @param session WebSocket 세션
	 * @param message 수신된 바이너리 메시지 (오디오 데이터)
	 */
	@Override
	public void receiveAudio(WebSocketSession session, BinaryMessage message) {
		ClientStream<StreamingRecognizeRequest> stream = streamMap.get(session.getId());
		if (stream == null) {
			log.warn("⚠[{}] 유효하지 않은 세션에서 오디오 수신", session.getId());
			return;
		}

		ByteString audioBytes = ByteString.copyFrom(message.getPayload().array());

		stream.send(
			StreamingRecognizeRequest.newBuilder()
				.setAudioContent(audioBytes)
				.build()
		);
	}

	/**
	 * WebSocket 세션이 종료될 때 호출됩니다.
	 * <p>
	 * STT 스트리밍을 종료하고 리소스를 정리합니다.
	 *
	 * @param session WebSocket 세션
	 */
	@Override
	public void end(WebSocketSession session) {
		ClientStream<StreamingRecognizeRequest> stream = streamMap.remove(session.getId());
		if (stream != null) {
			stream.closeSend();
			log.info("[{}] STT 스트리밍 종료", session.getId());
		}
	}
}
