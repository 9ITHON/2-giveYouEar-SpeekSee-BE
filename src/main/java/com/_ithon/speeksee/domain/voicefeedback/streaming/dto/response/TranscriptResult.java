package com._ithon.speeksee.domain.voicefeedback.streaming.service.port.dto.response;

/**
 * 음성 인식 결과를 나타내는 DTO 클래스입니다.
 * <p>
 * 이 클래스는 음성 인식 결과의 텍스트와 해당 텍스트의 신뢰도 점수를 포함합니다.
 */

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TranscriptResult {
	private final String transcript;
	private final float confidence;
	private final boolean isFinal;
}
