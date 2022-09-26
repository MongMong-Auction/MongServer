package com.mmserver.exception;

/**
 * 사용자 이메일 중복된 경우 발생
 */
public class DuplicationEmailException extends RuntimeException {

    @Override
    public String getMessage() {
        return "이미 가입된 이메일입니다.";
    }
}
