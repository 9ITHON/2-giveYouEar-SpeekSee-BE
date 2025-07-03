package com._ithon.speeksee.domain.member.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com._ithon.speeksee.domain.Script.domain.Script;
import com._ithon.speeksee.global.entity.BaseTimeEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String passwordHash;

	private String currentLevel ="초급";

	private Integer totalExp = 0;

	private LocalDate lastLogin;

	private Integer consecutiveDays;

	@OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
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
