package com._ithon.speeksee.domain.Script.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com._ithon.speeksee.domain.Script.entity.DifficultyLevel;
import com._ithon.speeksee.domain.Script.entity.Script;
import com._ithon.speeksee.domain.Script.entity.ScriptCategory;
import com._ithon.speeksee.domain.Script.service.ScriptService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/scripts")
@RequiredArgsConstructor
public class ScriptController {

	private final ScriptService scriptService;

	@PostMapping("/generate")
	public Script generateScript(@RequestParam ScriptCategory category, @RequestParam DifficultyLevel difficulty) {
		return scriptService.createScript(category, difficulty);
	}
}
