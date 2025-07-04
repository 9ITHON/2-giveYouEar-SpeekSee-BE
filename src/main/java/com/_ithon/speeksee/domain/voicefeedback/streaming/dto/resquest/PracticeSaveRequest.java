package com._ithon.speeksee.domain.voicefeedback.streaming.dto.resquest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PracticeSaveRequest {

	@Schema(description = "대본 ID", example = "1")
	private Long scriptId;

	@Schema(description = "전체 인식 텍스트", example = "안녕하세요. 오늘 날씨가 좋습니다.")
	private String transcript;

	@Schema(description = "정확도 (0.0 ~ 1.0)", example = "0.87")
	private double accuracy;

	@Schema(description = "오디오 파일 URL", example = "https://s3.amazonaws.com/bucket/audio1.mp3")
	private String audioUrl;

	@Schema(description = "단어별 연습 정보")
	private List<PracticeWordDto> words;
}
