package com._ithon.speeksee.domain.member.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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

	private final LevelTestService levelTestService;

	public void process(Long memberId, List<WordInfoDto> wordInfos) {
		if (wordInfos == null || wordInfos.isEmpty()) {
			log.warn("📭 레벨 테스트 단어 정보가 없습니다. memberId={}", memberId);
			return;
		}

		int total = wordInfos.size();
		int correct = (int)wordInfos.stream().filter(WordInfoDto::isCorrect).count();
		double accuracy = (double)correct / total;

		Level level = Level.determineLevel(accuracy);

		log.info("📈 레벨 평가 결과: memberId={}, correct={}, total={}, accuracy={}, level={}",
			memberId, correct, total, accuracy, level);

		// 사용자 레벨 저장
		levelTestService.saveLevel(memberId, level);
	}

}
