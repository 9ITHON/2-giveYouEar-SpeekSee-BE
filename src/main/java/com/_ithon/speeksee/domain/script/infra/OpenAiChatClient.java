package com._ithon.speeksee.domain.script.infra;

import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com._ithon.speeksee.domain.script.port.LlmClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiChatClient implements LlmClient {

	private final WebClient openAiWebClient;

	/**
	 * GPT-4o 모델을 사용하여 주어진 프롬프트에 대한 응답을 생성합니다.
	 *
	 * @param prompt 사용자로부터 받은 입력 프롬프트
	 * @return GPT-4o 모델의 응답 내용
	 */
	@Override
	public String chat(String prompt, int maxTokens) {
		Map<String, Object> requestBody = Map.of(
			"model", "gpt-4o",
			"messages", new Object[] {
				Map.of("role", "system", "content", ""),
				Map.of("role", "user", "content", prompt)
			},
			"max_tokens", maxTokens
		);

		Map<String, Object> response = openAiWebClient.post()
			.uri("/chat/completions")
			.body(BodyInserters.fromValue(requestBody))
			.retrieve()
			.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
			})
			.block();

		log.info("GPT response: {}", response);

		try {
			return ((Map<String, Object>)((Map<String, Object>)((java.util.List<?>)response.get("choices")).get(0)).get(
				"message")).get("content").toString();
		} catch (Exception e) {
			log.error("Failed to parse GPT response", e);
			return "";
		}
	}
}
