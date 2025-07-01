package com._ithon.speeksee.domain.voicefeedback.sync.port;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com._ithon.speeksee.domain.voicefeedback.streaming.dto.response.TranscriptResult;

public interface SyncSttClient {

	/**
	 * Google Cloud Speech-to-Text API를 사용하여 오디오 파일을 텍스트로 변환합니다.
	 *
	 * @param audioFile 변환할 오디오 파일
	 * @return 변환된 텍스트와 신뢰도 점수를 포함하는 리스트
	 * @throws IOException 파일 읽기 오류 발생 시
	 */
	List<TranscriptResult> transcribe(File audioFile) throws IOException;
}