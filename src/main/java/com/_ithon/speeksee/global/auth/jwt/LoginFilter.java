package com._ithon.speeksee.global.auth.jwt;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com._ithon.speeksee.global.auth.dto.request.LoginRequestDto;
import com._ithon.speeksee.global.infra.exception.code.ErrorCode;
import com._ithon.speeksee.global.infra.exception.response.ApiRes;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;
	private final ObjectMapper objectMapper;

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
		throws AuthenticationException {
		try {
			LoginRequestDto loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequestDto.class);

			UsernamePasswordAuthenticationToken token =
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

			return authenticationManager.authenticate(token); // 실제 인증 수행
		} catch (IOException e) {
			throw new RuntimeException("로그인 요청 처리 실패", e);
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		FilterChain chain, Authentication authResult) throws IOException {
		String username = authResult.getName();
		String token = jwtTokenProvider.generateAccessToken(username);

		response.addHeader("Authorization", "Bearer " + token);
		response.setContentType("application/json");
		response.getWriter().write("{\"token\": \"" + token + "\"}");
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request,
		HttpServletResponse response,
		AuthenticationException failed) throws IOException {

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // HTTP 401

		// 모두 AUTH_FAIL 코드 사용
		String message;

		if (failed instanceof BadCredentialsException) {
			message = "아이디 또는 비밀번호가 올바르지 않습니다.";
		} else if (failed instanceof DisabledException) {
			message = "비활성화된 계정입니다.";
		} else {
			message = "로그인에 실패했습니다.";
		}

		ApiRes<Object> errorResponse = ApiRes.failure(HttpStatus.UNAUTHORIZED, message, ErrorCode.AUTH_FAIL);

		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}

}
