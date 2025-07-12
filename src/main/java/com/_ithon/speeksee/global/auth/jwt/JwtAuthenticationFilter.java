package com._ithon.speeksee.global.auth.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com._ithon.speeksee.global.auth.service.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final CustomUserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain)
		throws ServletException, IOException {

		// 🔍 Authorization 헤더 확인
		String token = resolveToken(request);
		System.out.println("[JWT Filter] Authorization 헤더: " + request.getHeader("Authorization"));
		System.out.println("[JWT Filter] 파싱된 토큰: " + token);

		if (token != null && jwtTokenProvider.validateToken(token)) {
			System.out.println("[JWT Filter] ✅ 토큰 유효함");

			String type = jwtTokenProvider.getTokenType(token);
			System.out.println("[JWT Filter] 토큰 타입: " + type);

			// refresh 토큰은 인증을 건너뜀
			if (!"access".equals(type)) {
				System.out.println("[JWT Filter] ⏭️ refresh 토큰이므로 인증 생략");
				filterChain.doFilter(request, response);
				return;
			}

			String email = jwtTokenProvider.getEmailFromToken(token);
			System.out.println("[JWT Filter] 이메일 추출됨: " + email);

			UserDetails userDetails = userDetailsService.loadUserByUsername(email);
			System.out.println("[JWT Filter] UserDetails 로드 완료: " + userDetails.getUsername());

			// Spring Security 인증 객체 생성
			UsernamePasswordAuthenticationToken authentication =
				new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

			SecurityContextHolder.getContext().setAuthentication(authentication);
			System.out.println("[JWT Filter] ✅ SecurityContextHolder 인증 객체 설정됨");
		} else {
			System.out.println("[JWT Filter] ❌ 토큰 없음 또는 유효하지 않음");
		}

		filterChain.doFilter(request, response);
	}

	// Authorization 헤더에서 Bearer 토큰 추출
	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

}
