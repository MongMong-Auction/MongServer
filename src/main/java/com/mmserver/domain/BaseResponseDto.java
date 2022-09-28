package com.mmserver.domain;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 *
 */
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BaseResponseDto<T> implements Serializable {

    /**
     * 응답 메시지
     */
    private String message;

    /**
     * 응답 데이터
     */
    private T result;
}