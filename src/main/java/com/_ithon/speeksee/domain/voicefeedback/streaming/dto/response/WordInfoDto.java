package com._ithon.speeksee.domain.voicefeedback.streaming.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WordInfoDto {
	private String word;
	private double startTime; // 초 단위
	private double endTime;
	private boolean isCorrect;
}
