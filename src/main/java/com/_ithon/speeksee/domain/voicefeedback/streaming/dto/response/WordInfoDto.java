package com._ithon.speeksee.domain.voicefeedback.streaming.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WordInfoDto {
	@Schema(description = "인식된 단어", example = "안녕하세요")
	private String word;

	@Schema(description = "단어 시작 시간 (초)", example = "0.25")
	private double startTime;

	@Schema(description = "단어 종료 시간 (초)", example = "0.80")
	private double endTime;

	@Schema(description = "정답 여부 (true: 정답, false: 오답)", example = "true")
	private boolean isCorrect;
}
