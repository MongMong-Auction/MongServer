package com.mmserver.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 로그인 객체
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class LoginDTO {

    private String email;

    private String password;
}
