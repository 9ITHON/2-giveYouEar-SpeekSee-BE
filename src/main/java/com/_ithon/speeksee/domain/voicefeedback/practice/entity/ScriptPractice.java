package com._ithon.speeksee.domain.voicefeedback.practice.entity;

import java.util.ArrayList;
import java.util.List;

import com._ithon.speeksee.domain.member.entity.Member;
import com._ithon.speeksee.domain.script.domain.Script;
import com._ithon.speeksee.global.entity.BaseTimeEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
public class ScriptPractice extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 어떤 사용자
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	// 어떤 스크립트
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "script_id")
	private Script script;

	// 전체 인식된 텍스트
	@Column(columnDefinition = "TEXT", nullable = false)
	private String transcript;

	// 정확도 점수 (예: 0.87)
	private double accuracy;

	// 단어별 피드백 (연관관계 주인 아님)
	@Builder.Default
	@OneToMany(mappedBy = "practice", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PracticeWord> wordList = new ArrayList<>();

	public void addPracticeWord(PracticeWord word) {
		wordList.add(word);
		word.setPractice(this);
	}
}
