package com.mmserver.domain;

import lombok.*;

import java.io.Serializable;

/**
 * 사용자 정보
 */
@Setter
@Getter
@NoArgsConstructor
@ToString
public class UserInfoDTO implements Serializable {

    /**
     * 사용자 아이디(Primary Key)
     */
    private String email;

    /**
     * 사용자 이름
     */
    private String userName;

    /**
     * 포인트
     */
    private long point;

    /**
     * 테마 설정 값
     */
    private int theme;

    @Builder
    public UserInfoDTO(String email, String userName, long point, int theme) {
        this.email    = email;
        this.userName = userName;
        this.point    = point;
        this.theme    = theme;
    }
}
