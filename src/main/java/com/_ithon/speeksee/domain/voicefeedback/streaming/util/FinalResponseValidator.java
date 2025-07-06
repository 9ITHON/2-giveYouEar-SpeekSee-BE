package com._ithon.speeksee.domain.voicefeedback.streaming.util;

import java.util.List;

import com._ithon.speeksee.domain.voicefeedback.streaming.dto.response.WordInfoDto;

public class FinalResponseValidator {

	public static boolean isMeaningfulFinalResponse(List<WordInfoDto> words, String transcript, float confidence) {
		return hasNonEmptyTranscript(transcript);
	}

	private static boolean hasNonEmptyTranscript(String transcript) {
		return transcript != null && !transcript.trim().isEmpty();
	}
}