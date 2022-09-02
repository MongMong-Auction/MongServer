package com.mmserver.exception;

/**
 * 사용자 이름 중복된 경우 발생
 */
public class DuplicationUserNameExceiption extends RuntimeException {

    @Override
    public String getMessage() {
        return "이미 가입된 이름입니다.";
    }
}
