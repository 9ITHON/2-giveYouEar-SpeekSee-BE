package com._ithon.speeksee.domain.script.port;

public interface LlmClient {
	String chat(String prompt, int maxTokens);
}
