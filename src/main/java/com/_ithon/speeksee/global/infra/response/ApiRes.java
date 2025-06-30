package com._ithon.speeksee.global.infra.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiRes<T> {
	private boolean success;
	private T data;
	private String message;

	public static <T> ApiRes<T> success(T data) {
		return new ApiRes<>(true, data, "요청이 성공적으로 처리되었습니다.");
	}

	public static <T> ApiRes<T> success(T data, String message) {
		return new ApiRes<>(true, data, message);
	}

	public static <T> ApiRes<T> failure(String message) {
		return new ApiRes<>(false, null, message);
	}
}
