package com._ithon.speeksee.domain.script.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com._ithon.speeksee.domain.member.entity.Member;
import com._ithon.speeksee.domain.script.domain.Script;
import com._ithon.speeksee.domain.script.domain.ScriptSortOption;
import com._ithon.speeksee.domain.script.dto.response.ScriptResponse;
import com._ithon.speeksee.domain.script.dto.resquest.ScriptGenerateRequest;
import com._ithon.speeksee.domain.script.service.ScriptService;
import com._ithon.speeksee.global.auth.model.CustomUserDetails;
import com._ithon.speeksee.global.infra.exception.response.ApiRes;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

	@Operation(summary = "대본 생성", description = "카테고리: NEWS(뉴스), WEATHER(날씨), SELF_INTRODUCTION(자기소개), DAILY(일상) 난이도: EASY(쉬움), MEDIUM(중간), HARD(어려움)")
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

	@GetMapping("/my")
	public ApiRes<List<ScriptResponse>> getMyScripts(
		@AuthenticationPrincipal CustomUserDetails userDetails,

		@Parameter(
			description = """
        정렬 기준 (기본값: CREATED_DESC)
        - CREATED_DESC: 생성일 최신순
        - CREATED_ASC: 생성일 오래된 순
        - COUNT_DESC: 연습 횟수 많은 순
        - COUNT_ASC: 연습 횟수 적은 순
        - TITLE_ASC: 제목 가나다순
        - TITLE_DESC: 제목 역순
        - DIFFICULTY_ASC: 난이도 쉬운 순
        - DIFFICULTY_DESC: 난이도 어려운 순
        - UPDATED_ASC: 마지막 연습 오래된 순
        - UPDATED_DESC: 마지막 연습 최신순
        """
		)
		@RequestParam(defaultValue = "CREATED_DESC") ScriptSortOption sort
	) {
		List<Script> scripts = scriptService.getScriptsByMemberId(userDetails.getMember().getId(), sort);
		List<ScriptResponse> response = scripts.stream()
			.map(ScriptResponse::from)
			.toList();
		return ApiRes.success(response);
	}


	@Operation(
		summary = "내 대본 단건 조회",
		description = "로그인한 사용자의 특정 대본을 ID로 조회합니다."
	)
	@GetMapping("/{scriptId}")
	public ApiRes<ScriptResponse> getScriptById(
		@PathVariable Long scriptId,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		Script script = scriptService.getScriptByIdAndMemberId(scriptId, userDetails.getMember().getId());
		return ApiRes.success(ScriptResponse.from(script));
	}

	@Operation(
		summary = "대본 삭제",
		description = "해당 ID의 대본을 삭제합니다."
	)
	@DeleteMapping("/{scriptId}")
	public ApiRes<Void> deleteScript(
		@PathVariable("scriptId") Long scriptId,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		scriptService.deleteScriptByIdAndMemberId(scriptId, userDetails.getMember().getId());
		return ApiRes.success(null, "대본이 성공적으로 삭제되었습니다.");
	}

	@Operation(summary = "레벨 테스트 스크립트 조회", description = "레벨 테스트용 정적 스크립트를 무작위로 반환합니다.")
	@GetMapping("/level-test")
	public ApiRes<ScriptResponse> getLevelTestScript() {
		Script script = scriptService.getLevelTestScript();
		return ApiRes.success(ScriptResponse.from(script));
	}

}
