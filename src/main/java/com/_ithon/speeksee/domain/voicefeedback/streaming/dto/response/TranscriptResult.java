package com._ithon.speeksee.domain.voicefeedback.streaming.dto.response;

/**
 * 음성 인식 결과를 나타내는 DTO 클래스입니다.
 * <p>
 * 이 클래스는 음성 인식 결과의 텍스트와 해당 텍스트의 신뢰도 점수를 포함합니다.
 */

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranscriptResult {
	@Schema(description = "인식된 문장 텍스트", example = "안녕하세요. 오늘 날씨가 좋습니다.")
	private String transcript;

	@Schema(description = "음성 인식 신뢰도 (0.0 ~ 1.0)", example = "0.95")
	private float confidence;

	@Schema(description = "최종 결과 여부 (true: 최종 인식 결과)", example = "true")
	private boolean isFinal;

	@Schema(description = "인식된 단어 및 타이밍 정보 리스트")
	private List<WordInfoDto> words;

	@Schema(description = "맞은 단어 수", example = "4")
	private Integer correctCount;

	@Schema(description = "전체 단어 수", example = "5")
	private Integer totalCount;

	@Schema(description = "정확도 비율 (0.0 ~ 1.0)", example = "0.8")
	private Double accuracy;
}
