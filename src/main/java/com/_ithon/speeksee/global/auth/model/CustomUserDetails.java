package com._ithon.speeksee.global.auth.model;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com._ithon.speeksee.domain.member.entity.Member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

	private final Member member;

	//사용자가 아무 권한도 가지고 있지 않다
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.emptyList();
	}

	@Override
	public String getPassword() {
		return member.getPasswordHash();
	}

	@Override
	public String getUsername() {
		return member.getEmail(); // 로그인에 사용할 값
	}

	// 계정 상태 관련 설정(true로 반환하면 계정 활성 상태로 처리)
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public Long getUserId() {
		return member.getId();
	}
}
