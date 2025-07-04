package com._ithon.speeksee.domain.voicefeedback.streaming.infra.response;

import java.io.IOException;
import java.util.List;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com._ithon.speeksee.domain.voicefeedback.streaming.dto.response.TranscriptResult;
import com._ithon.speeksee.domain.voicefeedback.streaming.dto.response.WordInfoDto;
import com._ithon.speeksee.domain.voicefeedback.streaming.model.SttSessionContext;
import com._ithon.speeksee.domain.voicefeedback.streaming.service.PracticeSaveService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.cloud.speech.v1.StreamingRecognitionResult;
import com.google.cloud.speech.v1.StreamingRecognizeResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GoogleSttResponseObserver implements ResponseObserver<StreamingRecognizeResponse> {

	private final WebSocketSession session;
	private final SttSessionContext context;
	private final PracticeSaveService practiceSaveService;
	private final ObjectMapper objectMapper;
	private final List<String> scriptWords;

	public GoogleSttResponseObserver(
		WebSocketSession session,
		SttSessionContext context,
		PracticeSaveService practiceSaveService,
		ObjectMapper objectMapper,
		List<String> scriptWords
	) {
		this.session = session;
		this.context = context;
		this.practiceSaveService = practiceSaveService;
		this.objectMapper = objectMapper;
		this.scriptWords = scriptWords;
	}

	@Override
	public void onStart(StreamController controller) {
		context.controller = controller;
		log.info("🎙STT 스트리밍 시작: {}", session.getId());
	}

	@Override
	public void onResponse(StreamingRecognizeResponse response) {
		for (StreamingRecognitionResult result : response.getResultsList()) {
			if (result.getAlternativesCount() == 0) continue;

			var alt = result.getAlternatives(0);
			String transcript = alt.getTranscript();
			float confidence = alt.getConfidence();
			boolean isFinal = result.getIsFinal();

			if (!session.isOpen()) {
				log.warn("[{}] 세션이 닫혀 응답 생략됨", session.getId());
				return;
			}

			log.info("[{}] >>> STT 응답 (final: {}): {}", session.getId(), isFinal, transcript);

			List<WordInfoDto> words = alt.getWordsList().stream()
				.map(w -> {
					String spoken = w.getWord();
					int index = context.currentWordIndex.getAndIncrement();
					String expected = (index < scriptWords.size()) ? scriptWords.get(index) : "";

					return WordInfoDto.builder()
						.word(spoken)
						.startTime(w.getStartTime().getSeconds() + w.getStartTime().getNanos() / 1e9)
						.endTime(w.getEndTime().getSeconds() + w.getEndTime().getNanos() / 1e9)
						.isCorrect(spoken.equals(expected))
						.build();
				}).toList();

			double accuracy = words.isEmpty() ? 0.0 :
				(double) words.stream().filter(WordInfoDto::isCorrect).count() / words.size();

			if (isFinal) {
				practiceSaveService.save(context.memberId, context.scriptId, transcript, accuracy, words);
			}

			TranscriptResult dto = TranscriptResult.builder()
				.transcript(transcript)
				.confidence(confidence)
				.isFinal(isFinal)
				.words(words)
				.build();

			try {
				String json = objectMapper.writeValueAsString(dto);
				session.sendMessage(new TextMessage(json));
				log.info("[{}] 전송: {} (final: {})", session.getId(), transcript, isFinal);
			} catch (IOException e) {
				log.error("[{}] WebSocket 응답 전송 실패", session.getId(), e);
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
}
