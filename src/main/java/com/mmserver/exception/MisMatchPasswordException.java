package com.mmserver.exception;

/**
 * 사용자 확인 시, 저장된 비밀번호가 다를경우 발생
 */
public class MisMatchPasswordException extends RuntimeException {

    @Override
    public String getMessage() {
        return "비밀번호를 잘못 입력했습니다.";
    }
}
