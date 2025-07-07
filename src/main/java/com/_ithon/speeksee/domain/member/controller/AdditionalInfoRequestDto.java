package com._ithon.speeksee.domain.member.controller;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "소셜 사용자 추가 정보 입력 DTO")
public class AdditionalInfoRequestDto {

	@Schema(description = "닉네임", example = "ayla123")
	@NotBlank
	private String nickname;

	@Schema(description = "생년월일 (yyyy-MM-dd)", example = "2005-07-01")
	@NotNull
	private LocalDate birthdate;

}
