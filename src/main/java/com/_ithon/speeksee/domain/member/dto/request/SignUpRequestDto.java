package com._ithon.speeksee.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "회원가입 요청 DTO")
public class SignUpRequestDto {

	@Schema(description = "사용자의 이메일", example = "user@example.com", required = true)
	@Email(message = "올바른 이메일 형식이 아닙니다.")
	@NotBlank(message = "이메일은 필수입니다.")
	private String email;

	@Schema(description = "사용자의 비밀번호", example = "1234secure", required = true)
	@NotBlank(message = "비밀번호는 필수입니다.")
	private String password;

	@Schema(description = "사용자의 이름 또는 닉네임", example = "홍길동", required = true)
	@NotBlank(message = "이름은 필수입니다.")
	private String username;
}
