package com.mmserver.exception;

/**
 * 이메일로 조회된 사용자 정보가 없는 경우 발생
 */
public class NotFoundEmailException extends RuntimeException {

    @Override
    public String getMessage() {
        return "존재하지 않는 사용자입니다.";
    }
}
