package com._ithon.speeksee.global.auth.service;

import com._ithon.speeksee.domain.member.entity.Member;
import com._ithon.speeksee.global.auth.dto.request.LoginRequestDto;
import com._ithon.speeksee.global.auth.dto.response.LoginResponseDto;

public interface AuthService {
	LoginResponseDto login(LoginRequestDto loginRequestDto);

	LoginResponseDto login(Member member);

	LoginResponseDto refreshAccessToken(String refreshToken);

}
