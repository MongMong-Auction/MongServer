package com.mmserver.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 로그인 객체
 */
@Getter
@AllArgsConstructor
@ToString
public class LoginDto implements Serializable {

    /**
     * 사용자 아이디
     */
    @Email
    private String email;

    /**
     * 비밀번호
     */
    @NotBlank
    private String password;
}
