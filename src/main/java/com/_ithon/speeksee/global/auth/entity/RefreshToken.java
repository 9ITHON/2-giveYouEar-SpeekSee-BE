package com._ithon.speeksee.global.auth.entity;

import java.time.LocalDateTime;

import com._ithon.speeksee.domain.member.entity.Member;
import com._ithon.speeksee.global.entity.BaseTimeEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class RefreshToken{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	private String token;

	private boolean used;

	private LocalDateTime expiryDate;

}
