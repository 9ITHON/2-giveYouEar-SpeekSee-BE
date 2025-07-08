// package com._ithon.speeksee.domain.voicefeedback.sync.infra;
//
// import java.io.File;
// import java.io.FileInputStream;
// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.List;
//
// import org.springframework.stereotype.Component;
//
// import com._ithon.speeksee.domain.voicefeedback.streaming.dto.response.TranscriptResult;
// import com._ithon.speeksee.domain.voicefeedback.sync.port.SyncSttClient;
// import com.google.cloud.speech.v1.RecognitionAudio;
// import com.google.cloud.speech.v1.RecognitionConfig;
// import com.google.cloud.speech.v1.RecognizeResponse;
// import com.google.cloud.speech.v1.SpeechClient;
// import com.google.protobuf.ByteString;
//
// /**
//  * Google Cloud Speech-to-Text 동기식 클라이언트 구현
//  * * 이 클래스는 Google Cloud Speech-to-Text API를 사용하여 오디오 파일을 텍스트로 변환합니다.
//  */
// @Component
// public class GoogleSyncSttClient implements SyncSttClient {
//
// 	/**
// 	 * Google Cloud Speech-to-Text API를 사용하여 오디오 파일을 텍스트로 변환합니다.
// 	 *
// 	 * @param audioFile 변환할 오디오 파일
// 	 * @return 변환된 텍스트와 신뢰도 점수를 포함하는 리스트
// 	 * @throws IOException 파일 읽기 오류 발생 시
// 	 */
// 	@Override
// 	public List<TranscriptResult> transcribe(File audioFile) throws IOException {
// 		List<TranscriptResult> resultList = new ArrayList<>();
//
// 		try (SpeechClient speechClient = SpeechClient.create()) {
// 			RecognitionConfig config = RecognitionConfig.newBuilder()
// 				.setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
// 				.setLanguageCode("ko-KR")
// 				.setSampleRateHertz(16000)
// 				.build();
//
// 			ByteString audioBytes = ByteString.readFrom(new FileInputStream(audioFile));
//
// 			RecognitionAudio audio = RecognitionAudio.newBuilder()
// 				.setContent(audioBytes)
// 				.build();
//
// 			RecognizeResponse response = speechClient.recognize(config, audio);
//
// 			for (SpeechRecognitionResult result : response.getResultsList()) {
// 				SpeechRecognitionAlternative alternative = result.getAlternatives(0);
// 				resultList.add(new TranscriptResult(
// 					alternative.getTranscript(),
// 					alternative.getConfidence(),
// 					true, //임시
// 					null // 단어 정보는 동기식 API에서는 제공되지 않음
//
// 				));
// 			}
// 		}
//
// 		return resultList;
// 	}
// }
