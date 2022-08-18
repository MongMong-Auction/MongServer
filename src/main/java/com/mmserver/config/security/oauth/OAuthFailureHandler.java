package com.mmserver.config.security.oauth;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *  OAuth2 인증 실패 시, 후 처리를 위한 클래스
 *  {@link AuthenticationFailureHandler}을 구현
 */
public class OAuthFailureHandler implements AuthenticationFailureHandler {

    /**
     * OAuth 인증 실패 시
     * HttpStatus 400 + 에러 메시지 응답
     *
     * @param request   : 로그인 시, 요청
     * @param response  : 응답 객체
     * @param exception : 인증요청 거부하기 위해 던져진 예외
     * @throws IOException : I/O 시, 오류가 발생한 경우
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }
}
