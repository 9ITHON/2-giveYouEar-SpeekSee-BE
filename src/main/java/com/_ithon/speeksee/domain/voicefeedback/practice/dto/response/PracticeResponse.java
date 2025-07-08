package com._ithon.speeksee.domain.voicefeedback.practice.dto.response;

import java.util.List;

import com._ithon.speeksee.domain.voicefeedback.practice.dto.request.PracticeWordDto;
import com._ithon.speeksee.domain.voicefeedback.practice.entity.ScriptPractice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PracticeResponse {
	private Long id;
	private String transcript;
	private double accuracy;

	private List<PracticeWordDto> words;

	public static PracticeResponse fromEntity(ScriptPractice entity) {
		return PracticeResponse.builder()
			.id(entity.getId())
			.transcript(entity.getTranscript())
			.accuracy(entity.getAccuracy())
			.words(
				entity.getWordList().stream()
					.map(word -> new PracticeWordDto(
						word.getWord(),
						word.getStartTime(),
						word.getEndTime(),
						word.isCorrect()
					)).toList()
			)
			.build();
	}
}

