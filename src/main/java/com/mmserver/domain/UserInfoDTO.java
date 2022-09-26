package com.mmserver.domain;

import lombok.*;

import java.io.Serializable;

/**
 * 사용자 정보
 */
@Getter
@NoArgsConstructor
@ToString
public class UserInfoDTO implements Serializable {

    /**
     * 식별 값(PK)
     */
    private Long id;

    /**
     * 사용자 아이디
     */
    private String email;

    /**
     * 사용자 이름
     */
    private String userName;

    /**
     * 포인트
     */
    private int point;

    /**
     * 테마 설정 값
     */
    private int theme;

    @Builder
    public UserInfoDTO(Long id, String email, String userName, int point, int theme) {
        this.id       = id;
        this.email    = email;
        this.userName = userName;
        this.point    = point;
        this.theme    = theme;
    }
}
