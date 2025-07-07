package com._ithon.speeksee.domain.member.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com._ithon.speeksee.domain.script.domain.Script;
import com._ithon.speeksee.global.entity.BaseTimeEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	@Column(unique = true)
	private String nickname;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = true)
	private String passwordHash;

	private LocalDate birthday;

	@Enumerated(EnumType.STRING)
	private AuthProvider authProvider; // GOOGLE, KAKAO, NAVER 등

	@Column
	private String providerId; // 각 소셜 서비스에서 제공하는 고유 ID

	@Builder.Default
	private String currentLevel = "초급";

	@Builder.Default
	private Integer totalExp = 0;

	private LocalDate lastLogin;

	private Integer consecutiveDays;

	@Builder.Default
	private boolean isInfoCompleted = false; // 추가 정보가 입력되었는지

	public void completeAdditionalInfo(String nickname, LocalDate birthday) {
		this.nickname = nickname;
		this.birthday = birthday;
		this.isInfoCompleted = true;
	}

	@OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Script> scripts = new ArrayList<>();

	public void addScript(Script script) {
		scripts.add(script);
		script.setAuthor(this);
	}

	public void removeScript(Script script) {
		scripts.remove(script);
		script.setAuthor(null);
	}
}