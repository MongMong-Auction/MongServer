package com.mmserver.exception;

/**
 * 사용자 이름 중복된 경우 발생
 */
public class DuplicationUserNameException extends RuntimeException {

    @Override
    public String getMessage() {
        return "이미 가입된 닉네임입니다.";
    }
}
