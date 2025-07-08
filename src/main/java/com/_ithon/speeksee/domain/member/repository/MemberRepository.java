package com._ithon.speeksee.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com._ithon.speeksee.domain.member.entity.AuthProvider;
import com._ithon.speeksee.domain.member.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByEmail(String email);

	Optional<Member> findByProviderIdAndAuthProvider(String providerId, AuthProvider provider);

	// 이메일 중복 확인
	boolean existsByEmail(String email);

	boolean existsByNickname(String nickname);
}
