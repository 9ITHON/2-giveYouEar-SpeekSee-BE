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
import com._ithon.speeksee.global.infra.exception.response.ApiRes;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/practices")
@RequiredArgsConstructor
@Tag(name = "Practice", description = "연습 기록 API")
public class PracticeController {

	private final PracticeService practiceService;

	@Operation(summary = "사용자 연습 기록 전체 조회", description = "회원 ID로 사용자의 전체 연습 기록을 조회합니다.")
	@GetMapping("/user/{memberId}")
	public ResponseEntity<ApiRes<List<PracticeResponse>>> getAllByMember(
		@Parameter(description = "회원 ID", example = "1") @PathVariable Long memberId
	) {
		List<PracticeResponse> result = practiceService.findByMemberId(memberId);
		return ResponseEntity.ok(ApiRes.success(result));
	}

	@Operation(summary = "단건 연습 기록 조회", description = "연습 기록 ID로 단일 연습 데이터를 조회합니다.")
	@GetMapping("/{practiceId}")
	public ResponseEntity<ApiRes<PracticeResponse>> getOne(
		@Parameter(description = "연습 기록 ID", example = "100") @PathVariable Long practiceId
	) {
		PracticeResponse result = practiceService.findById(practiceId);
		return ResponseEntity.ok(ApiRes.success(result));
	}

	@Operation(summary = "연습 기록 삭제", description = "연습 기록 ID와 회원 ID를 이용해 특정 연습 기록을 삭제합니다.")
	@DeleteMapping("/{practiceId}")
	public ResponseEntity<ApiRes<Void>> delete(
		@Parameter(description = "연습 기록 ID", example = "100") @PathVariable Long practiceId,
		@Parameter(description = "회원 ID", example = "1") @RequestParam Long memberId
	) {
		practiceService.deleteById(practiceId, memberId);
		return ResponseEntity.ok(ApiRes.success(null));
	}
}
