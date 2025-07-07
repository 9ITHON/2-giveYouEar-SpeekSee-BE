package com._ithon.speeksee.domain.voicefeedback.streaming.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PracticeWordDto {

	@Schema(description = "단어 텍스트", example = "안녕하세요")
	private String word;

	@Schema(description = "시작 시간 (초)", example = "0.25")
	private double startTime;

	@Schema(description = "종료 시간 (초)", example = "0.80")
	private double endTime;

	@Schema(description = "정답 여부", example = "true")
	private boolean isCorrect;
}
