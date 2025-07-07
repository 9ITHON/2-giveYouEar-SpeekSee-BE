package com._ithon.speeksee.domain.member.dto.request;

import java.time.LocalDate;

import com._ithon.speeksee.domain.member.entity.Member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

	@Schema(description = "사용자의 닉네임", example = "길동길동", required = true)
	@NotBlank(message = "닉네임은 필수입니다.")
	private String nickname;

	@Schema(description = "사용자의 생년월일", example = "2025-07-07", required = true)
	@NotNull(message = "생년월일은 필수입니다.")
	private LocalDate birthday;

	public Member toEntity(String encodedPassword) {
		return Member.builder()
			.email(this.email)
			.passwordHash(encodedPassword)
			.nickname(this.nickname)
			.birthday(this.birthday)
			.currentLevel("초급")
			.totalExp(0)
			.consecutiveDays(0)
			.isInfoCompleted(true)
			.build();
	}
}
