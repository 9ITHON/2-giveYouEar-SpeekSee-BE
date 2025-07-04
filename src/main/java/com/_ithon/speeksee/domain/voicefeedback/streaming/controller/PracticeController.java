package com._ithon.speeksee.domain.voicefeedback.streaming.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com._ithon.speeksee.domain.voicefeedback.streaming.dto.response.PracticeResponse;
import com._ithon.speeksee.domain.voicefeedback.streaming.service.PracticeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/practices")
@RequiredArgsConstructor
public class PracticeController {

	private final PracticeService practiceService;

	// [1] 사용자 연습 기록 전체 조회
	@GetMapping("/user/{memberId}")
	public ResponseEntity<List<PracticeResponse>> getAllByMember(@PathVariable Long memberId) {
		return ResponseEntity.ok(practiceService.findByMemberId(memberId));
	}

	// [2] 단건 연습 기록 조회
	@GetMapping("/{practiceId}")
	public ResponseEntity<PracticeResponse> getOne(@PathVariable Long practiceId) {
		return ResponseEntity.ok(practiceService.findById(practiceId));
	}

	@DeleteMapping("/{practiceId}")
	public ResponseEntity<Void> delete(@PathVariable Long practiceId, @RequestParam Long memberId) {
		practiceService.deleteById(practiceId, memberId);
		return ResponseEntity.noContent().build();
	}

}
