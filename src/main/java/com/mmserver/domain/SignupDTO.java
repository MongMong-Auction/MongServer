package com.mmserver.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Id;
import java.io.Serializable;

/**
 * 회원가입 객체
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class SignupDTO implements Serializable {

    /**
     * 사용자 아이디(Primary Key)
     */
    @Id
    private String email;

    /**
     * 비밀번호
     */
    private String password;

    /**
     * 사용자 이름
     */
    private String userName;
}
