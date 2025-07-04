package com._ithon.speeksee.domain.voicefeedback.streaming.infra.sender;

import org.springframework.stereotype.Component;

import com._ithon.speeksee.domain.voicefeedback.streaming.model.SttSessionContext;
import com.google.cloud.speech.v1.StreamingRecognizeRequest;
import com.google.protobuf.ByteString;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class StreamingRequestSender {

	public void sendAudio(SttSessionContext context, ByteString audioBytes) {
		if (context == null || context.requestStream == null) {
			log.warn("오디오 전송 실패: 세션이 유효하지 않음");
			return;
		}

		try {
			StreamingRecognizeRequest request = StreamingRecognizeRequest.newBuilder()
				.setAudioContent(audioBytes)
				.build();

			context.requestStream.send(request);
			log.debug("오디오 전송 완료 (bytes: {})", audioBytes.size());

		} catch (Exception e) {
			log.error("오디오 전송 중 예외 발생", e);
		}
	}
}
