package com._ithon.speeksee.global.infra.exception.auth;

import com._ithon.speeksee.global.infra.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class SpeekseeAuthExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        }
        catch(SpeekseeAuthException e) {
            handleflowfitAuthException(response, e);
        }
        catch(AuthenticationException e) {
            handleAuthenticationException(response);
        }
    }

    private void handleflowfitAuthException(HttpServletResponse response, SpeekseeAuthException e) throws IOException {
        response.setStatus(e.getStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = ErrorResponse.of(e.getStatus().value(), "FLOWFIT_AUTH_EXCEPTION", e.getMessage());

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    private void handleAuthenticationException(HttpServletResponse response) throws IOException {
        response.setStatus(401);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = ErrorResponse.of(401, "TEACHMON_AUTH_EXCEPTION", "알 수 없는 인증 오류가 발생했습니다.");

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
