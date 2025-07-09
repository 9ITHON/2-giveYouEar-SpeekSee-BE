package com._ithon.speeksee.domain.voicefeedback.streaming.infra.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

import com._ithon.speeksee.domain.script.repository.ScriptRepository;
import com._ithon.speeksee.domain.member.repository.MemberRepository;
import com._ithon.speeksee.domain.voicefeedback.streaming.infra.response.GoogleSttResponseObserver;
import com._ithon.speeksee.domain.member.service.LevelTestProcessor;
import com._ithon.speeksee.domain.voicefeedback.streaming.infra.sender.StreamingRequestSender;
import com._ithon.speeksee.domain.voicefeedback.streaming.infra.sender.WebSocketErrorSender;
import com._ithon.speeksee.domain.voicefeedback.streaming.infra.session.AuthenticatedSession;
import com._ithon.speeksee.domain.voicefeedback.streaming.infra.session.SttSessionManager;
import com._ithon.speeksee.domain.voicefeedback.streaming.infra.session.SttSessionContext;
import com._ithon.speeksee.domain.voicefeedback.streaming.port.StreamingSttClient;
import com._ithon.speeksee.domain.voicefeedback.practice.service.PracticeSaveService;
import com._ithon.speeksee.global.infra.exception.entityException.MemberNotFoundException;
import com._ithon.speeksee.global.infra.exception.entityException.ScriptNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.cloud.speech.v1.StreamingRecognitionConfig;
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

	private final PracticeSaveService practiceSaveService;
	private final MemberRepository memberRepository;
	private final ScriptRepository scriptRepository;
	private final SttSessionManager sessionManager;
	private final StreamingRequestSender requestSender;
	private final WebSocketErrorSender errorSender;
	private final LevelTestProcessor levelTestProcessor;

	/**
	 * Google Cloud Speech-to-Text 클라이언트를 초기화합니다.
	 * <p>
	 * Google Cloud 인증 정보를 사용하여 SpeechClient를 생성합니다.
	 *
	 * @param credentialsPath Google Cloud 인증 JSON 파일 경로
	 * @throws IOException 인증 파일을 읽는 중 오류가 발생하면 예외가 발생합니다.
	 */
	public GoogleStreamingSttClient(
		@Value("${google.credentials.path}") String credentialsPath,
		PracticeSaveService practiceSaveService,
		MemberRepository memberRepository,
		ScriptRepository scriptRepository,
		SttSessionManager sessionManager,
		StreamingRequestSender requestSender,
		WebSocketErrorSender errorSender,
		LevelTestProcessor levelTestProcessor
	) throws IOException {
		GoogleCredentials credentials = GoogleCredentials
			.fromStream(new FileInputStream(credentialsPath))
			.createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

		SpeechSettings settings = SpeechSettings.newBuilder()
			.setCredentialsProvider(FixedCredentialsProvider.create(credentials))
			.build();

		this.speechClient = SpeechClient.create(settings);
		this.practiceSaveService = practiceSaveService;
		this.memberRepository = memberRepository;
		this.scriptRepository = scriptRepository;
		this.sessionManager = sessionManager;
		this.requestSender = requestSender;
		this.errorSender = errorSender;
		this.levelTestProcessor = levelTestProcessor;
	}

	/**
	 * WebSocket 세션이 시작될 때 호출됩니다.
	 * <p>
	 * STT 스트리밍 세션을 초기화하고 필요한 파라미터를 설정합니다.
	 *
	 * @param session WebSocket 세션
	 */
	@Override
	public void start(WebSocketSession session) {
		try {
			SttSessionContext context = sessionManager.startSession(session);
			AuthenticatedSession auth = (AuthenticatedSession)session.getAttributes().get("auth");
			if (auth != null) {
				context.mode = auth.getMode(); // level_test 또는 normal
			}

			if (context.memberId == null || context.scriptId == null) {
				errorSender.sendErrorAndClose(session, "AUTH_001", "인증 정보가 없습니다. AUTH 메시지를 먼저 보내야 합니다.");
				return;
			}

			memberRepository.findById(context.memberId).orElseThrow(() -> {
				log.warn("[{}] 존재하지 않는 memberId: {}", session.getId(), context.memberId);
				return new MemberNotFoundException();
			});

			// script 조회 및 전처리
			String rawScript = scriptRepository.findById(context.scriptId)
				.orElseThrow(() -> {
					log.warn("[{}] 존재하지 않는 scriptId: {}", session.getId(), context.scriptId);
					return new ScriptNotFoundException();
				}).getContent();

			// 전처리: 특수문자 제거, 공백 정규화
			String preprocessed = rawScript.replaceAll("[^가-힣\\s]", "").replaceAll("\\s+", " ").trim();
			List<String> scriptWords = List.of(preprocessed.split(" "));

			// 응답 옵저버 생성
			ResponseObserver<StreamingRecognizeResponse> responseObserver =
				new GoogleSttResponseObserver(session, context, practiceSaveService, objectMapper, scriptWords,
					errorSender, levelTestProcessor);

			// 클라이언트 스트림 생성
			ClientStream<StreamingRecognizeRequest> clientStream =
				speechClient.streamingRecognizeCallable().splitCall(responseObserver);

			// 초기 설정 전송
			clientStream.send(StreamingRecognizeRequest.newBuilder()
				.setStreamingConfig(StreamingRecognitionConfig.newBuilder()
					.setConfig(RecognitionConfig.newBuilder()
						.setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
						.setLanguageCode("ko-KR")
						.setSampleRateHertz(SAMPLE_RATE)
						.setEnableWordTimeOffsets(true)
						.build())
					.setInterimResults(true)
					.setSingleUtterance(false)
					.build())
				.build());

			context.requestStream = clientStream;

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
		SttSessionContext context = sessionManager.getSession(session.getId());
		if (context == null || context.requestStream == null) {
			errorSender.sendErrorAndClose(session, "SESSION_INVALID", "유효하지 않은 세션입니다.");
			return;
		}

		ByteString audioBytes = ByteString.copyFrom(message.getPayload().array());
		log.debug("🎙 받은 오디오 크기 (bytes): {}", audioBytes.size());

		// StreamingRequestSender를 통해 전송
		requestSender.sendAudio(context, audioBytes);
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
		sessionManager.closeSession(session);
	}
}
