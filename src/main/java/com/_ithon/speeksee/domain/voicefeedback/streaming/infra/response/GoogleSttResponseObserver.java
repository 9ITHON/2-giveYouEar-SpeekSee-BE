package com._ithon.speeksee.domain.voicefeedback.streaming.infra.response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com._ithon.speeksee.domain.voicefeedback.streaming.dto.response.TranscriptResult;
import com._ithon.speeksee.domain.voicefeedback.streaming.dto.response.WordInfoDto;
import com._ithon.speeksee.domain.voicefeedback.streaming.infra.sender.WebSocketErrorSender;
import com._ithon.speeksee.domain.voicefeedback.streaming.infra.session.SttSessionContext;
import com._ithon.speeksee.domain.voicefeedback.practice.service.PracticeSaveService;
import com._ithon.speeksee.domain.voicefeedback.streaming.util.FinalResponseValidator;
import com._ithon.speeksee.domain.voicefeedback.streaming.util.LcsAligner;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.StreamingRecognitionResult;
import com.google.cloud.speech.v1.StreamingRecognizeResponse;
import com.google.cloud.speech.v1.WordInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GoogleSttResponseObserver implements ResponseObserver<com.google.cloud.speech.v1.StreamingRecognizeResponse> {

	private final WebSocketSession session;
	private final SttSessionContext context;
	private final PracticeSaveService practiceSaveService;
	private final ObjectMapper objectMapper;
	private final List<String> scriptWords;
	private final WebSocketErrorSender errorSender;

	public GoogleSttResponseObserver(
		WebSocketSession session,
		SttSessionContext context,
		PracticeSaveService practiceSaveService,
		ObjectMapper objectMapper,
		List<String> scriptWords,
		WebSocketErrorSender errorSender
	) {
		this.session = session;
		this.context = context;
		this.practiceSaveService = practiceSaveService;
		this.objectMapper = objectMapper;
		this.scriptWords = scriptWords;
		this.errorSender = errorSender;
	}

	@Override
	public void onStart(StreamController controller) {
		context.controller = controller;
		log.info("🎙 STT 스트리밍 시작: {}", session.getId());
	}

	@Override
	public void onResponse(StreamingRecognizeResponse response) {
		for (StreamingRecognitionResult result : response.getResultsList()) {
			if (result.getAlternativesCount() == 0) continue;

			SpeechRecognitionAlternative alt = result.getAlternatives(0);
			String transcript = alt.getTranscript();
			float confidence = alt.getConfidence();
			boolean isFinal = result.getIsFinal();

			if (!session.isOpen()) {
				log.warn("[{}] 세션이 닫혀 응답 생략됨", session.getId());
				return;
			}

			log.info("[{}] >>> STT 응답 (final: {}): {}", session.getId(), isFinal, transcript);

			List<String> spokenWords = new ArrayList<>();
			List<Double> startTimes = new ArrayList<>();
			List<Double> endTimes = new ArrayList<>();

			for (WordInfo word : alt.getWordsList()) {
				spokenWords.add(word.getWord().trim());
				startTimes.add(word.getStartTime().getSeconds() + word.getStartTime().getNanos() / 1e9);
				endTimes.add(word.getEndTime().getSeconds() + word.getEndTime().getNanos() / 1e9);
			}

			List<WordInfoDto> wordInfos = LcsAligner.align(spokenWords, scriptWords, startTimes, endTimes);

			int correct = (int) wordInfos.stream().filter(WordInfoDto::isCorrect).count();
			int total = wordInfos.size();
			double accuracy = total == 0 ? 0.0 : (double) correct / total;

			if (isFinal && FinalResponseValidator.isMeaningfulFinalResponse(wordInfos, transcript, confidence)) {
				practiceSaveService.save(context.memberId, context.scriptId, transcript, accuracy, wordInfos);
			}

			try {
				TranscriptResult.TranscriptResultBuilder builder = TranscriptResult.builder()
					.transcript(transcript)
					.confidence(confidence)
					.isFinal(isFinal)
					.words(wordInfos);

				if (isFinal) {
					builder.correctCount(correct)
						.totalCount(total)
						.accuracy(accuracy);
				}

				TranscriptResult dto = builder.build();

				session.sendMessage(new TextMessage(objectMapper.writeValueAsString(dto)));
				log.info("[{}] 전송: {} (final: {})", session.getId(), transcript, isFinal);

			} catch (IOException e) {
				log.error("[{}] WebSocket 응답 전송 실패", session.getId(), e);
			}
		}
	}

	@Override
	public void onComplete() {
		log.info("🎙 STT 스트리밍 완료: {}", session.getId());
	}

	@Override
	public void onError(Throwable t) {
		log.error("STT 스트리밍 오류: {}", session.getId(), t);
		errorSender.sendErrorAndClose(session, "STT_ERROR", "STT 처리 중 오류가 발생했습니다.");
	}
}
