package com.mmserver.domain.EnumType;

import org.springframework.http.HttpStatus;

/**
 * 에러 코드 Enum
 */
public enum ErrorCode {
    // JWT
    /**
     * Access Token 없음
     */
    JWT_ACCESS_TOKEN_EMPTY(HttpStatus.UNAUTHORIZED.value(), "Access Token이 존재하지 않습니다."),

    /**
     * Access Token 만료
     */
    JWT_ACCESS_TOKEN_EXPIRED(701, "만료된 Access Token 입니다."),

    /**
     * Refresh Token 만료
     */
    JWT_REFRESH_TOKEN_EXPIRED(702, "만료된 Refresh Token 입니다.");

    private final int status;

    private final String msg;

    ErrorCode(int status, String msg) {
        this.status = status;
        this.msg    = msg;
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }
}
