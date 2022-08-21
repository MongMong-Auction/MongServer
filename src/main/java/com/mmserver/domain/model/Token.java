package com.mmserver.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Date;

/**
 * JWT 관리를 위한 객체
 */
@Getter
@NoArgsConstructor
@ToString
@RedisHash("auth")
public class Token {

    /**
     * 사용자 key 값(PK)
     */
    @Id
    private String key;

    /**
     * JWT 토큰
     */
    private String token;

    /**
     * JWT 만료 시간
     */
    private Date tokenExpireTime;

    @Builder
    public Token(String key, String token, Date tokenExpireTime) {
        this.key             = key;
        this.token           = token;
        this.tokenExpireTime = tokenExpireTime;
    }
}


