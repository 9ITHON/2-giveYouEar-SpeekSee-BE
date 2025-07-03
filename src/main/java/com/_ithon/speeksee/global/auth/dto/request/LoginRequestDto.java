package com._ithon.speeksee.global.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "로그인 요청 DTO")
public class LoginRequestDto {

	@Schema(description = "사용자의 이메일", example = "user@example.com", required = true)
	@NotBlank(message = "이메일은 필수입니다.")
	@Email(message = "올바른 이메일 형식이 아닙니다.")
	private String email;    // 로그인 시, 사용하는 ID

	@Schema(description = "사용자의 비밀번호", example = "1234secure", required = true)
	@NotBlank(message = "비밀번호는 필수입니다.")
	private String password;
}