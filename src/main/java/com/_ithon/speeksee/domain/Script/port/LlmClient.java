package com._ithon.speeksee.domain.Script.port;

public interface LlmClient {
	String chat(String prompt, int maxTokens);
}
