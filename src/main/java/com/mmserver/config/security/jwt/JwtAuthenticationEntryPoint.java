package com.mmserver.config.security.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Security 인증실패(HttpStatus : 401) 시, 후 처리를 위한 클래스
 */
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Security 인증 실패 시
     * HttpStatus 401 + 에러 메시지 응답
     *
     * @param request       : JWT 인증 시, 요청 객체
     * @param response      : 응답 객체
     * @param authException : 인증실패 예외
     * @throws IOException : I/O 시, 오류가 발생한 경우
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.error("Jwt 인증실패");
        log.error("Error Message => {}", authException.getMessage());

        response.sendError(HttpStatus.UNAUTHORIZED.value(), "잘못된 인증정보입니다.");
    }
}
