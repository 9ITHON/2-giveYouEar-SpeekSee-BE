package com._ithon.speeksee.domain.voicefeedback.sync.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com._ithon.speeksee.domain.voicefeedback.streaming.dto.response.TranscriptResult;
import com._ithon.speeksee.domain.voicefeedback.sync.service.SttService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stt-test")
public class SttTestController {

	private final SttService sttService;

	@PostMapping("/file")
	public ResponseEntity<List<TranscriptResult>> transcribe(@RequestParam("file") MultipartFile file) throws
		IOException {
		// 임시 파일로 저장
		File temp = File.createTempFile("stt-", ".wav");
		file.transferTo(temp);

		List<TranscriptResult> result = sttService.transcribe(temp);
		return ResponseEntity.ok(result);
	}
}
