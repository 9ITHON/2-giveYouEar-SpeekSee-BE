package com._ithon.speeksee.domain.voicefeedback.dto.response;

/**
 * 음성 인식 결과를 나타내는 DTO 클래스입니다.
 * <p>
 * 이 클래스는 음성 인식 결과의 텍스트와 해당 텍스트의 신뢰도 점수를 포함합니다.
 */
public class TranscriptResult {

	private final String transcript;
	private final float confidence;

	public TranscriptResult(String transcript, float confidence) {
		this.transcript = transcript;
		this.confidence = confidence;
	}

	public String getTranscript() {
		return transcript;
	}

	public float getConfidence() {
		return confidence;
	}

	@Override
	public String toString() {
		return "TranscriptResult{" +
			"transcript='" + transcript + '\'' +
			", confidence=" + confidence +
			'}';
	}
}
