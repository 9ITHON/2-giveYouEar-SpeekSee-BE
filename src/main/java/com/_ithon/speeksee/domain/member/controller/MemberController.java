package com._ithon.speeksee.domain.member.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._ithon.speeksee.domain.member.dto.request.SignUpRequestDto;
import com._ithon.speeksee.domain.member.entity.Member;
import com._ithon.speeksee.domain.member.service.MemberService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class MemberController {

	private final MemberService memberService;

	public MemberController(MemberService memberService) {
		this.memberService = memberService;
	}

	// TODO : APIResponse 구조로 변경하기
	@PostMapping("/signup")
	public ResponseEntity<Map<String, Object>> signup(@RequestBody @Valid SignUpRequestDto signUpRequestDto) {
		Member savedMember = memberService.signUp(signUpRequestDto);

		// data에 들어갈 사용자 정보 구성
		Map<String, Object> data = new HashMap<>();
		data.put("userId", savedMember.getId());
		data.put("email", savedMember.getEmail());
		data.put("username", savedMember.getUsername());

		// 전체 응답 구조 구성
		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		response.put("data", data);
		response.put("message", "회원가입이 완료되었습니다");

		return ResponseEntity.ok(response);
	}

	// @PostMapping("/signup")
	// public ResponseEntity<ApiResponse<Void>> signup(@RequestBody @Valid SignUpRequestDto signUpRequestDto) {
	// 	memberService.signUp(signUpRequestDto);
	//
	// 	ApiResponse<Void> response = new ApiResponse<>(
	// 		true,
	// 		null,
	// 		"회원가입이 성공적으로 완료되었습니다."
	// 	);
	// 	return ResponseEntity.ok(response);
	// }
}
