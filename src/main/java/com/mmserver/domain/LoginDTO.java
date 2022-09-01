package com.mmserver.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 로그인 객체
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class LoginDTO implements Serializable {

    /**
     * 사용자 아이디
     */
    private String email;

    /**
     * 비밀번호
     */
    private String password;
}
