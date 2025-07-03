package com._ithon.speeksee.global.auth.service;

import com._ithon.speeksee.global.auth.dto.request.LoginRequestDto;
import com._ithon.speeksee.global.auth.dto.response.LoginResponseDto;

public interface AuthService {
	LoginResponseDto login(LoginRequestDto loginRequestDto);

	LoginResponseDto refreshAccessToken(String refreshToken);

}
