package com._ithon.speeksee.domain.voicefeedback.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com._ithon.speeksee.domain.voicefeedback.dto.response.TranscriptResult;
import com._ithon.speeksee.domain.voicefeedback.port.SyncSttClient;

import lombok.RequiredArgsConstructor;

/**
 * 음성 인식 서비스
 * <p>
 * 이 서비스는 동기식 음성 인식 클라이언트를 사용하여 오디오 파일을 텍스트로 변환합니다.
 */
@Service
@RequiredArgsConstructor
public class SttService {

	private final SyncSttClient syncSttClient;

	public List<TranscriptResult> transcribe(File audioFile) throws IOException {
		return syncSttClient.transcribe(audioFile);
	}
}