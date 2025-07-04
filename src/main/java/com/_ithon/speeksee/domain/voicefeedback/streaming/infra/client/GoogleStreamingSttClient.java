package com._ithon.speeksee.domain.voicefeedback.streaming.infra.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

import com._ithon.speeksee.domain.Script.repository.ScriptRepository;
import com._ithon.speeksee.domain.member.repository.MemberRepository;
import com._ithon.speeksee.domain.voicefeedback.streaming.infra.response.GoogleSttResponseObserver;
import com._ithon.speeksee.domain.voicefeedback.streaming.infra.session.SttSessionManager;
import com._ithon.speeksee.domain.voicefeedback.streaming.model.SttSessionContext;
import com._ithon.speeksee.domain.voicefeedback.streaming.port.StreamingSttClient;
import com._ithon.speeksee.domain.voicefeedback.streaming.service.PracticeSaveService;
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

	// 개발용 더미 스크립트
	private final String dummyScript = "안녕하세요 오늘 날씨는 맑습니다";
	private final List<String> scriptWords = List.of(dummyScript.split(" "));


	private final PracticeSaveService practiceSaveService;
	private final MemberRepository memberRepository;
	private final ScriptRepository scriptRepository;
	private final SttSessionManager sessionManager;

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
		SttSessionManager sessionManager
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

			SttSessionContext context = sessionManager.startSession(session);

			String memberIdStr = getQueryParam(session, "memberId");
			String scriptIdStr = getQueryParam(session, "scriptId");



			if (memberIdStr == null || scriptIdStr == null) {
				log.warn("[{}] WebSocket 파라미터 누락 (memberId 또는 scriptId)", session.getId());
				session.close();
				return;
			}

			context.memberId = Long.parseLong(memberIdStr);
			context.scriptId = Long.parseLong(scriptIdStr);

			if (!memberRepository.existsById(context.memberId)) {
				log.warn("[{}] 존재하지 않는 memberId: {}", session.getId(), context.memberId);
				throw new MemberNotFoundException();
			}

			if (!scriptRepository.existsById(context.scriptId)) {
				log.warn("[{}] 존재하지 않는 scriptId: {}", session.getId(), context.scriptId);
				throw new ScriptNotFoundException();
			}

			context.memberId = Long.parseLong(memberIdStr);
			context.scriptId = Long.parseLong(scriptIdStr);

			ResponseObserver<StreamingRecognizeResponse> responseObserver =
				new GoogleSttResponseObserver(session, context, practiceSaveService, objectMapper, scriptWords);


			ClientStream<StreamingRecognizeRequest> clientStream =
				speechClient.streamingRecognizeCallable().splitCall(responseObserver);

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

			context.client = speechClient;
			context.requestStream = clientStream;

		} catch (Exception e) {
			log.error("STT 스트리밍 세션 생성 실패: {}", session.getId(), e);
		}
	}

	/**
	 * WebSocket 세션에서 쿼리 파라미터를 추출합니다.
	 * <p>
	 * 예: "memberId=1&scriptId=3"에서 memberId 또는 scriptId 값을 추출합니다.
	 *
	 * @param session WebSocket 세션
	 * @param key     추출할 파라미터 키 (예: "memberId" 또는 "scriptId")
	 * @return 해당 키의 값, 없으면 null
	 */
	private String getQueryParam(WebSocketSession session, String key) {
		String query = session.getUri().getQuery(); // 예: "memberId=1&scriptId=3"
		if (query == null) return null;

		for (String param : query.split("&")) {
			String[] pair = param.split("=");
			if (pair.length == 2 && pair[0].equals(key)) {
				return pair[1];
			}
		}
		return null;
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
			log.warn("⚠[{}] 유효하지 않은 세션에서 오디오 수신 (context: {}, requestStream: {})",
				session.getId(),
				context,
				(context != null ? context.requestStream : null));
			return;
		}

		ByteString audioBytes = ByteString.copyFrom(message.getPayload().array());
		log.debug("🎙 받은 오디오 크기 (bytes): {}", audioBytes.size());

		context.requestStream.send(
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
		sessionManager.closeSession(session);
	}
}
