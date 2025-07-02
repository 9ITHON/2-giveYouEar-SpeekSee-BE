package com._ithon.speeksee.global.auth.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com._ithon.speeksee.domain.member.dto.response.MemberInfoResponseDto;
import com._ithon.speeksee.domain.member.entity.Member;
import com._ithon.speeksee.domain.member.repository.MemberRepository;
import com._ithon.speeksee.global.auth.dto.request.LoginRequestDto;
import com._ithon.speeksee.global.auth.dto.response.LoginResponseDto;
import com._ithon.speeksee.global.auth.entity.RefreshToken;
import com._ithon.speeksee.global.auth.jwt.JwtTokenProvider;
import com._ithon.speeksee.global.auth.repository.RefreshTokenRepository;
import com._ithon.speeksee.global.infra.exception.auth.SpeekseeAuthException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;
	private final MemberRepository memberRepository;
	private final RefreshTokenRepository refreshTokenRepository;


	@Override
	public LoginResponseDto login(LoginRequestDto loginRequestDto) {
		Authentication authentication = authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword())
		);

		Member member = memberRepository.findByEmail(loginRequestDto.getEmail())
			.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

		String accessToken = jwtTokenProvider.generateAccessToken(member.getId(), member.getEmail());
		String refreshTokenValue  = jwtTokenProvider.generateRefreshToken(member.getId(), member.getEmail());

		RefreshToken refreshToken = RefreshToken.builder()
			.token(refreshTokenValue)
			.member(member)
			.expiryDate(LocalDateTime.now().plusDays(7)) // 예시: 7일
			.used(false)
			.build();

		refreshTokenRepository.save(refreshToken);

		// 5. access token 만료 시간 (예: ms 단위 → s 단위 변환)
		int expiresIn = (int)(jwtTokenProvider.getAccessTokenExpirationMs() / 1000); // 또는 직접 상수로 설정 가능

		// 6. 사용자 정보를 DTO로 변환
		MemberInfoResponseDto memberInfoDto = new MemberInfoResponseDto(member);

		return new LoginResponseDto(accessToken, refreshTokenValue, expiresIn, memberInfoDto);
	}

	@Override
	public LoginResponseDto refreshAccessToken(String refreshToken) {
		// 1. 유효성 검사
		if (refreshToken == null || refreshToken.isBlank()) {
			throw new SpeekseeAuthException(HttpStatus.UNAUTHORIZED, "Refresh Token이 존재하지 않습니다.");
		}

		if (!jwtTokenProvider.validateToken(refreshToken)) {
			throw new SpeekseeAuthException(HttpStatus.UNAUTHORIZED, "유효하지 않은 Refresh Token입니다.");
		}

		RefreshToken existingToken = refreshTokenRepository.findByToken(refreshToken)
			.orElseThrow(() -> new SpeekseeAuthException(HttpStatus.UNAUTHORIZED, "등록되지 않은 리프레시 토큰입니다."));

		if (existingToken.isUsed() || existingToken.getExpiryDate().isBefore(LocalDateTime.now())) {
			throw new SpeekseeAuthException(HttpStatus.UNAUTHORIZED, "만료되었거나 이미 사용된 리프레시 토큰입니다.");
		}

		Member member = existingToken.getMember();

		existingToken.setUsed(true);
		refreshTokenRepository.save(existingToken);

		// 3. 새 access token 발급
		String newAccessToken = jwtTokenProvider.generateAccessToken(member.getId(), member.getEmail());
		String newRefreshToken = jwtTokenProvider.generateRefreshToken(member.getId(), member.getEmail());

		RefreshToken newToken = RefreshToken.builder()
			.token(newRefreshToken)
			.member(member)
			.expiryDate(LocalDateTime.now().plusDays(7))
			.used(false)
			.build();

		refreshTokenRepository.save(newToken);

		int expiresIn = (int)(jwtTokenProvider.getAccessTokenExpirationMs() / 1000);
		MemberInfoResponseDto memberInfoDto = new MemberInfoResponseDto(member);

		return new LoginResponseDto(newAccessToken, refreshToken, expiresIn, memberInfoDto);
	}

	// @Override
	// public String getUserName(CustomUserDetails customUserDetails) {
	// 	Long userId = customUserDetails.getUsername();
	// 	return memberRepository.getUserName();
	// }

}
