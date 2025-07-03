package com._ithon.speeksee.domain.voicefeedback.streaming.dto.response;

/**
 * 음성 인식 결과를 나타내는 DTO 클래스입니다.
 * <p>
 * 이 클래스는 음성 인식 결과의 텍스트와 해당 텍스트의 신뢰도 점수를 포함합니다.
 */

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranscriptResult {
	private String transcript;
	private float confidence;
	private boolean isFinal;

	private List<WordInfoDto> words;
}
