// package com._ithon.speeksee.domain.voicefeedback.sync.controller;
//
// import java.io.File;
// import java.io.IOException;
// import java.util.List;
//
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.multipart.MultipartFile;
//
// import com._ithon.speeksee.domain.voicefeedback.streaming.dto.response.TranscriptResult;
// import com._ithon.speeksee.domain.voicefeedback.sync.service.SttService;
//
// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.Parameter;
// import io.swagger.v3.oas.annotations.media.ArraySchema;
// import io.swagger.v3.oas.annotations.media.Content;
// import io.swagger.v3.oas.annotations.media.Schema;
// import io.swagger.v3.oas.annotations.responses.ApiResponse;
// import lombok.RequiredArgsConstructor;
//
// @RestController
// @RequiredArgsConstructor
// @RequestMapping("/api/stt-test")
// public class SttTestController {
//
// 	private final SttService sttService;
//
// 	@Operation(
// 		summary = "STT 파일 업로드 테스트",
// 		description = "음성 파일(WAV)을 업로드하여 텍스트로 변환합니다.",
// 		responses = {
// 			@ApiResponse(
// 				responseCode = "200",
// 				description = "음성 인식 성공",
// 				content = @Content(
// 					mediaType = "application/json",
// 					array = @ArraySchema(schema = @Schema(implementation = TranscriptResult.class))
// 				)
// 			)
// 		}
// 	)
// 	@PostMapping("/file")
// 	public ResponseEntity<List<TranscriptResult>> transcribe(
// 		@Parameter(
// 			description = "음성 파일 (WAV 형식)",
// 			required = true,
// 			content = @Content(mediaType = "multipart/form-data")
// 		)
// 		@RequestParam("file") MultipartFile file
// 	) throws IOException {
// 		// 임시 파일로 저장
// 		File temp = File.createTempFile("stt-", ".wav");
// 		file.transferTo(temp);
//
// 		List<TranscriptResult> result = sttService.transcribe(temp);
// 		return ResponseEntity.ok(result);
// 	}
// }
