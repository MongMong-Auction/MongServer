package com.mmserver.domain.mapper;

import java.io.Serializable;

/**
 * 사용자 정보 조회 Mapper
 */
public interface UserInfoMapping extends Serializable {
    /**
     * 사용자 아이디(Primary Key)
     */
    String getEmail();

    /**
     * 사용자 이름
     */
    String getUserName();

    /**
     * 포인트
     */
    long getPoint();

    /**
     * 테마 설정 값
     */
    int getTheme();
}
