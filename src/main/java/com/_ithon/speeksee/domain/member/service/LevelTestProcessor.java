package com._ithon.speeksee.domain.member.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;

import com._ithon.speeksee.domain.member.entity.Level;
import com._ithon.speeksee.domain.voicefeedback.streaming.dto.response.WordInfoDto;
import com._ithon.speeksee.domain.voicefeedback.streaming.infra.session.SttSessionContext;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LevelTestProcessor {

	final int LevelTestScriptNum = 3;
	private final LevelTestService levelTestService;
	private final Map<Long, List<Double>> accuracyMap = new ConcurrentHashMap<>();

	public void process(Long memberId, List<WordInfoDto> wordInfos) {

		// 정확도 계산 로직
		int total = wordInfos.size();
		int correct = (int)wordInfos.stream().filter(WordInfoDto::isCorrect).count();
		double accuracy = (double)correct / total;

		List<Double> accuracyList = accuracyMap.computeIfAbsent(memberId, k -> new ArrayList<>());
		accuracyList.add(accuracy);

		if (wordInfos.isEmpty()) {
			log.warn("📭 레벨 테스트 단어 정보가 없습니다. memberId={}", memberId);
			return;
		}

		log.info("📊 현재까지 받은 정확도 수: {} / {}", accuracyList.size(), LevelTestScriptNum);

		if (accuracyList.size() == LevelTestScriptNum) {
			double average = accuracyList.stream()
				.mapToDouble(Double::doubleValue)
				.average()
				.orElse(0.0);

			log.info("평균: {}", average);
			Level level = Level.determineLevel(average);

			// 사용자 레벨 저장
			levelTestService.saveLevel(memberId, level);

			accuracyMap.remove(memberId); // 누적 데이터 초기화
		}

		log.info("📈 레벨 평가 결과: memberId={}, correct={}, total={}, accuracy={}",
			memberId, correct, total, accuracy);

	}

}
