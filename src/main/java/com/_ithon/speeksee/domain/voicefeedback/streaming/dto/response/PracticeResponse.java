package com._ithon.speeksee.domain.voicefeedback.streaming.dto.response;

import java.util.List;

import com._ithon.speeksee.domain.voicefeedback.streaming.dto.resquest.PracticeWordDto;
import com._ithon.speeksee.domain.voicefeedback.streaming.entity.ScriptPractice;

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
	private String audioUrl;

	private List<PracticeWordDto> words;

	public static PracticeResponse fromEntity(ScriptPractice entity) {
		return PracticeResponse.builder()
			.id(entity.getId())
			.transcript(entity.getTranscript())
			.accuracy(entity.getAccuracy())
			.audioUrl(entity.getAudioUrl())
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

