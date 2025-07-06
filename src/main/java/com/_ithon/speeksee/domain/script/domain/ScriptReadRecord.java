package com._ithon.speeksee.domain.script.domain;

import com._ithon.speeksee.domain.member.entity.Member;
import com._ithon.speeksee.global.entity.BaseTimeEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Entity
@Getter
public class ScriptReadRecord extends BaseTimeEntity {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "script_id")
	private Script script;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member reader;

	@Lob
	private String wrongParts; // 예: JSON 문자열로 틀린 문장 목록 저장

	private Integer totalMistakes;

	// 추후 진행률이나 소요시간 등도 추가 가능
}
