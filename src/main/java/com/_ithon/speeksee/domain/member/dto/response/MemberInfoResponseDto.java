package com._ithon.speeksee.domain.member.dto.response;

import java.time.LocalDateTime;

import com._ithon.speeksee.domain.member.entity.Member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "사용자 정보 DTO (로그인 응답 및 내 정보 조회 공통 사용)")
public class MemberInfoResponseDto {

	@Schema(description = "사용자 ID", example = "1")
	private final Long userId;

	@Schema(description = "이메일", example = "user@example.com")
	private final String email;

	@Schema(description = "이름 또는 닉네임", example = "홍길동")
	private final String username;

	@Schema(description = "현재 레벨", example = "초급")
	private final String currentLevel;

	@Schema(description = "총 경험치", example = "1250")
	private final Integer totalExp;

	@Schema(description = "연속 출석 일수", example = "5")
	private final Integer consecutiveDays;

	@Schema(description = "계정 생성일시", example = "2025-06-20T10:00:00Z", required = false)
	private final LocalDateTime createdAt;  // 로그인 시점에는 null이어도 됨

	public MemberInfoResponseDto(Member member) {
		this.userId = member.getId();
		this.email = member.getEmail();
		this.username = member.getUsername();
		this.currentLevel = member.getCurrentLevel();
		this.totalExp = member.getTotalExp();
		this.consecutiveDays = member.getConsecutiveDays();
		this.createdAt = member.getCreatedAt();
	}

}
