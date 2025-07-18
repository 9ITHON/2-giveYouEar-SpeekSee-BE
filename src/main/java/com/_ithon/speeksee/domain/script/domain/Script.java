package com._ithon.speeksee.domain.script.domain;

import com._ithon.speeksee.domain.member.entity.Member;
import com._ithon.speeksee.global.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Script extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "script_id")
	private Long id;

	private String title; // 스크립트 제목

	@Column(columnDefinition = "TEXT", nullable = false)
	private String content; // 스크립트 내용

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member author;

	@Enumerated(EnumType.STRING)
	private ScriptCategory category;

	@Enumerated(EnumType.STRING)
	private DifficultyLevel difficultyLevel;

	@Column(nullable = false)
	@Builder.Default
	private boolean isLevelTest = false; // 레벨 테스트용

	@Column(nullable = false)
	private int practiceCount;

	public void increasePracticeCount() {
		this.practiceCount += 1;
	}

	public void decreasePracticeCount() {
		this.practiceCount = Math.max(0, this.practiceCount - 1);
	}
}
