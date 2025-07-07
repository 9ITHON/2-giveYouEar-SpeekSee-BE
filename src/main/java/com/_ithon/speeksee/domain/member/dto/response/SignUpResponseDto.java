package com._ithon.speeksee.domain.member.dto.response;

import java.time.LocalDate;

import com._ithon.speeksee.domain.member.entity.Member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "회원가입 응답 DTO")
public class SignUpResponseDto {

	@Schema(description = "사용자 ID", example = "1")
	private final Long userId;

	@Schema(description = "사용자 이메일", example = "user@example.com")
	private final String email;

	@Schema(description = "사용자 닉네임", example = "길동길동")
	private final String nickname;

	@Schema(description = "사용자 생년월일", example = "2025-07-07")
	private final LocalDate birthday;

	public static SignUpResponseDto from(Member member) {
		return SignUpResponseDto.builder()
			.userId(member.getId())
			.email(member.getEmail())
			.nickname(member.getNickname())
			.birthday(member.getBirthday())
			.build();
	}
}
