package com._ithon.speeksee.domain.Script.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com._ithon.speeksee.domain.Script.entity.DifficultyLevel;
import com._ithon.speeksee.domain.Script.entity.Script;
import com._ithon.speeksee.domain.Script.entity.ScriptCategory;
import com._ithon.speeksee.domain.Script.service.ScriptService;
import com._ithon.speeksee.global.infra.exception.response.ApiRes;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/scripts")
@RequiredArgsConstructor
public class ScriptController {

	private final ScriptService scriptService;

	@Operation(summary = "대본 생성", description = "카테고리와 난이도를 받아 대본을 생성합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "대본 생성 성공",
			content = @Content(schema = @Schema(implementation = ApiRes.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@PostMapping("/generate")
	public ApiRes<Script> generateScript(@RequestParam ScriptCategory category,
		@RequestParam DifficultyLevel difficulty) {
		Script script = scriptService.createScript(category, difficulty);
		return ApiRes.success(script);
	}
}
