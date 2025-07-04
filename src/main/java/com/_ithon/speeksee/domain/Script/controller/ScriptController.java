package com._ithon.speeksee.domain.Script.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._ithon.speeksee.domain.Script.domain.Script;
import com._ithon.speeksee.domain.Script.dto.response.ScriptResponse;
import com._ithon.speeksee.domain.Script.dto.resquest.ScriptGenerateRequest;
import com._ithon.speeksee.domain.Script.service.ScriptService;
import com._ithon.speeksee.domain.member.entity.Member;
import com._ithon.speeksee.global.auth.model.CustomUserDetails;
import com._ithon.speeksee.global.infra.exception.response.ApiRes;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/scripts")
@RequiredArgsConstructor
public class ScriptController {

	private final ScriptService scriptService;

	@Operation(summary = "대본 생성", description = "카테고리와 난이도를 받아 대본을 생성합니다.")
	@ApiResponse(responseCode = "200", description = "대본 생성 성공",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ApiRes.class),
			examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
				name = "성공 예시",
				value = """
						{
						  "success": true,
						  "data": {
							"id": 1,
							"title": "뉴스 대본",
							"content": "오늘 날씨는 맑습니다.",
							"category": "NEWS",
							"difficulty": "EASY",
							"authorEmail": "user@example.com"
						  },
						  "message": "요청이 성공적으로 처리되었습니다.",
						  "status": 200,
						  "code": 0,
						  "time": "2025-06-30T12:00:00Z"
						}
					"""
			)
		)
	)
	@PostMapping("/daily")
	public ApiRes<ScriptResponse> generateScript(
		@RequestBody @Valid ScriptGenerateRequest request,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		Member member = userDetails.getMember();
		Script script = scriptService.createScript(request.getCategory(), request.getDifficulty(), member.getId());
		return ApiRes.success(ScriptResponse.from(script));
	}

}
