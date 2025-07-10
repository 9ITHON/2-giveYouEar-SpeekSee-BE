package com._ithon.speeksee.domain.voicefeedback.streaming.infra.response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com._ithon.speeksee.domain.member.service.LevelTestProcessor;
import com._ithon.speeksee.domain.voicefeedback.practice.entity.PracticeMode;
import com._ithon.speeksee.domain.voicefeedback.practice.service.PracticeSaveService;
import com._ithon.speeksee.domain.voicefeedback.streaming.dto.response.TranscriptResult;
import com._ithon.speeksee.domain.voicefeedback.streaming.dto.response.WordInfoDto;
import com._ithon.speeksee.domain.voicefeedback.streaming.infra.sender.WebSocketErrorSender;
import com._ithon.speeksee.domain.voicefeedback.streaming.infra.session.SttSessionContext;
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
public class GoogleSttResponseObserver
	implements ResponseObserver<com.google.cloud.speech.v1.StreamingRecognizeResponse> {

	private final WebSocketSession session;
	private final SttSessionContext context;
	private final PracticeSaveService practiceSaveService;
	private final ObjectMapper objectMapper;
	private final List<String> scriptWords;
	private final WebSocketErrorSender errorSender;
	private final LevelTestProcessor levelTestProcessor;

	private final List<WordInfoDto> accumulatedWordInfos = new ArrayList<>();
	private final StringBuilder accumulatedTranscript = new StringBuilder();


	public GoogleSttResponseObserver(
		WebSocketSession session,
		SttSessionContext context,
		PracticeSaveService practiceSaveService,
		ObjectMapper objectMapper,
		List<String> scriptWords,
		WebSocketErrorSender errorSender,
		LevelTestProcessor levelTestProcessor
	) {
		this.session = session;
		this.context = context;
		this.practiceSaveService = practiceSaveService;
		this.objectMapper = objectMapper;
		this.scriptWords = scriptWords;
		this.errorSender = errorSender;
		this.levelTestProcessor = levelTestProcessor;
	}

	@Override
	public void onStart(StreamController controller) {
		context.controller = controller;
		log.info("🎙 STT 스트리밍 시작: {}", session.getId());
	}

	@Override
	public void onResponse(StreamingRecognizeResponse response) {
		for (StreamingRecognitionResult result : response.getResultsList()) {
			if (result.getAlternativesCount() == 0)
				continue;

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

			int correct = (int)wordInfos.stream().filter(WordInfoDto::isCorrect).count();
			int total = wordInfos.size();
			double accuracy = total == 0 ? 0.0 : (double)correct / total;

			if (isFinal && FinalResponseValidator.isMeaningfulFinalResponse(wordInfos, transcript, confidence)) {
				if (context.getMode() == PracticeMode.LEVEL_TEST) {
					context.levelTestWordInfos.addAll(wordInfos);
					log.info("[레벨테스트] 응답 누적: {}", transcript);
				} else {
					accumulatedTranscript.append(transcript).append(" ");
					accumulatedWordInfos.addAll(wordInfos);
					log.info("[일반연습] 문장 누적 중: {}", transcript);
				}
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
						.accuracy(accuracy)
						.type("INTERIM_FINAL");  // 또는 "ON_RESPONSE_FINAL"
				} else {
					builder.type("INTERIM"); // 중간 응답도 명시
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
		if (context.getMode() == PracticeMode.NORMAL) {
			flushAccumulatedResults();
		}

		if (context.getMode() == PracticeMode.LEVEL_TEST && !context.levelTestProcessed) {
			context.levelTestProcessed = true;
			try {
				String json = objectMapper.writeValueAsString(context.levelTestWordInfos);
				log.info("[{}] LEVEL_TEST 누적 WordInfo 전체: {}", session.getId(), json);
			} catch (Exception e) {
				log.warn("[{}] LEVEL_TEST WordInfo 직렬화 실패", session.getId(), e);
			}
			levelTestProcessor.process(context.memberId, context.levelTestWordInfos);
			context.levelTestWordInfos.clear();
			log.info("레벨 테스트 완료 처리됨");
		}
		log.info("🎙 STT 스트리밍 종료: {}", session.getId());
	}

	@Override
	public void onError(Throwable t) {
		log.error("STT 스트리밍 오류: {}", session.getId(), t);
		errorSender.sendErrorAndClose(session, "STT_ERROR", "STT 처리 중 오류가 발생했습니다.");
	}

	public void flushAccumulatedResults() {
		List<WordInfoDto> wordInfos;
		String fullTranscript;

		// [0] 모드에 따라 wordInfos, transcript 선택
		if (context.getMode() == PracticeMode.LEVEL_TEST) {
			if (context.levelTestWordInfos.isEmpty()) {
				log.warn("[{}] LEVEL_TEST - 누적된 단어 없음 → flush 생략", session.getId());
				return;
			}
			wordInfos = new ArrayList<>(context.levelTestWordInfos);
			fullTranscript = wordInfos.stream()
				.map(WordInfoDto::getWord)
				.collect(Collectors.joining(" "))
				.trim();
		} else {
			if (accumulatedWordInfos.isEmpty()) {
				log.warn("[{}] NORMAL - 누적된 단어 없음 → flush 생략", session.getId());
				return;
			}
			wordInfos = new ArrayList<>(accumulatedWordInfos);
			fullTranscript = accumulatedTranscript.toString().trim();
		}

		// [1] 정확도 계산
		int correct = (int) wordInfos.stream().filter(WordInfoDto::isCorrect).count();
		int total = wordInfos.size();
		double accuracy = total == 0 ? 0.0 : (double) correct / total;

		// [2] DB 저장
		practiceSaveService.save(
			context.getMemberId(),
			context.getScriptId(),
			fullTranscript,
			accuracy,
			wordInfos
		);

		// [3] 최종 응답 전송
		if (session != null && session.isOpen()) {
			try {
				TranscriptResult dto = TranscriptResult.builder()
					.transcript(fullTranscript)
					.confidence(1.0f)
					.isFinal(true)
					.words(wordInfos)
					.correctCount(correct)
					.totalCount(total)
					.accuracy(accuracy)
					.type("FINAL_FLUSH")
					.build();

				String json = objectMapper.writeValueAsString(dto);
				log.info("[{}] 최종 응답 DTO: {}", session.getId(), json);
				session.sendMessage(new TextMessage(json));
				log.info("[{}] 최종 응답 전송 완료", session.getId());

			} catch (IOException e) {
				log.error("[{}] 응답 전송 실패", session.getId(), e);
			}
		} else {
			log.warn("[{}] WebSocket 세션이 이미 닫혀 응답 전송 생략", session.getId());
		}

		// [4] 상태 초기화
		if (context.getMode() == PracticeMode.LEVEL_TEST) {
		} else {
			accumulatedTranscript.setLength(0);
			accumulatedWordInfos.clear();
		}
	}
}
