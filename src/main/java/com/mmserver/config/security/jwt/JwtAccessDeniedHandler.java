package com.mmserver.config.security.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Security 인가실패(HttpStatus : 403) 시, 후 처리를 위한 클래스
 */
@Slf4j
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * Security 인가 실패 시
     * HttpStatus 403 + 에러 메시지 응답
     *
     * @param request               : JWT 인증 시, 요청 객체
     * @param response              : 응답 객체
     * @param accessDeniedException : 인가실패 예외
     * @throws IOException : I/O 시, 오류가 발생한 경우
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        log.error("Jwt 인가실패");
        log.error("Error Message : {}", accessDeniedException.getMessage());

        response.sendError(HttpStatus.FORBIDDEN.value(), "잘못된 접근입니다.");
    }
}
