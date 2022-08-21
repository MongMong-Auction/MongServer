package com.mmserver.domain;

import com.mmserver.domain.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 민감한 정보를 제외한 사용자 정보
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

    /**
     * User 객체 UserInfoDTO 객체로 변환
     *
     * @param  user        : 전체 사용자 정보
     * @return UserInfoDTO : 세팅된 객체
     */
    public UserInfoDTO toUserInfo(User user) {
        this.email    = user.getEmail();
        this.userName = user.getUserName();
        this.point    = user.getPoint();
        this.theme    = user.getTheme();

        return this;
    }
}
