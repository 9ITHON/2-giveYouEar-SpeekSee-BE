package com._ithon.speeksee.domain.voicefeedback.practice.entity;

import jakarta.persistence.Entity;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PracticeWord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 소속된 연습 기록
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "practice_id")
	private ScriptPractice practice;

	// 단어 텍스트
	private String word;

	// 시작 시간 (초 단위)
	private double startTime;

	// 종료 시간 (초 단위)
	private double endTime;

	// 올바르게 발음했는지 여부
	private boolean isCorrect;

}
