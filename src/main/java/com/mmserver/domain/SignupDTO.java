package com.mmserver.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 회원가입 객체
 */
@Getter
@NoArgsConstructor
@ToString
public class SignupDTO implements Serializable {

    /**
     * 사용자 아이디
     */
    @Email(regexp = "이메일 형식이 아닙니다.")
    private String email;

    /**
     * 비밀번호
     */
    @NotBlank(message = "비밀번호는 필수 입력사항입니다.")
    private String password;

    /**
     * 사용자 이름
     */
    @NotBlank(message = "사용자 이름은 필수 입력사항입니다.")
    private String userName;
}
