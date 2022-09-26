package com.mmserver.exception;

/**
 * 지원하지않는 OAuth 기관 요청인 경우 발생
 */
public class NotSupportedOAuthException extends RuntimeException {

    @Override
    public String getMessage() {
        return "지원하지 않는 소셜서비스입니다.";
    }
}
