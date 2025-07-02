package com._ithon.speeksee.domain.Script.entity;

import com._ithon.speeksee.global.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Script extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "script_id")
	private Long id;

	private String title; // 스크립트 제목

	@Lob
	private String content; // 스크립트 내용

	// TODO: 스크립트와 유저 연관관계 설정 필요
	private String author; // 스크립트 작성자
	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "user_id")
	// private User author;

	@Enumerated(EnumType.STRING)
	private ScriptCategory category;

	@Enumerated(EnumType.STRING)
	private DifficultyLevel difficultyLevel;
}
