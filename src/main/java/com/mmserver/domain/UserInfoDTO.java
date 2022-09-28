package com.mmserver.domain;

import com.mmserver.domain.EnumType.RoleType;
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
     * 사용자 권한
     */
    private RoleType role;

    /**
     * 포인트
     */
    private int point;

    /**
     * 테마 설정 값
     */
    private int theme;

    @Builder
    public UserInfoDTO(Long id, String email, String userName, RoleType role, int point, int theme) {
        this.id       = id;
        this.email    = email;
        this.userName = userName;
        this.role     = role;
        this.point    = point;
        this.theme    = theme;
    }
}
