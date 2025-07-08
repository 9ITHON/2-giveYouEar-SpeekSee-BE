package com._ithon.speeksee.global.auth.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com._ithon.speeksee.domain.attendance.service.AttendanceService;
import com._ithon.speeksee.domain.member.dto.response.MemberInfoResponseDto;
import com._ithon.speeksee.domain.member.entity.AuthProvider;
import com._ithon.speeksee.domain.member.entity.Member;
import com._ithon.speeksee.global.auth.dto.response.LoginResponseDto;
import com._ithon.speeksee.global.auth.dto.response.OAuth2UserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginService {

	private final List<OAuth2Client> clients;
	private final OAuthService oAuthService;
	private final AuthService authService;
	private final AttendanceService attendanceService;

	public LoginResponseDto login(String code, AuthProvider authProvider) {

		OAuth2Client client = clients.stream()
			.filter(c -> c.getProvider() == authProvider) // fe에서 준 authProvider랑 맞는거만 통과
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("지원하지 않는 OAuth 공급자입니다: " + authProvider));

		String accessToken = client.getAccessToken(code);
		OAuth2UserInfo userInfo = client.getUserInfo(accessToken);

		log.info("소셜 로그인 시도: provider={}, email={}", authProvider, userInfo.getEmail());

		// providerId + provider으로 기존 회원 조회
		Optional<Member> optionalMember = oAuthService.findByProviderIdAndProvider(userInfo.getId(), authProvider);

		// 신규 회원 생성 또는 기존 회원 반환
		Member member = optionalMember.orElseGet(() ->
			oAuthService.findOrCreate(userInfo, authProvider)
		);
		
		// 출석
		attendanceService.attend(member);

		// 추가 정보가 없으면, 추가 정보가 필요
		if (!member.isInfoCompleted()) {
			LoginResponseDto tokens = authService.login(member);

			return LoginResponseDto.of(
				tokens.getAccessToken(),
				tokens.getRefreshToken(),
				tokens.getExpiresIn(),
				member
			);
		}

		// 로그인 -> jwt 발급
		return authService.login(member);
	}
}
