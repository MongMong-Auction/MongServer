package com.mmserver.config;

import com.mmserver.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Runtime Exception 관리 Hendler
 */
@Slf4j
@RestControllerAdvice
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RuntimeExceptionHandler {

    /**
     * {@link NotSupportedOAuthException} 예외 처리
     * @param e {@link NotSupportedOAuthException} 객체
     * @return String : message
     */
    @ExceptionHandler(NotSupportedOAuthException.class)
    public String notSupportedOAuthExceptionHandler(NotSupportedOAuthException e) {
        log.error("Status : {}", HttpStatus.BAD_REQUEST);
        log.error("Error Message : {}", e.getMessage());

        return e.getMessage();
    }

    /**
     * {@link NotFoundEmailException} 예외 처리
     * @param e {@link NotFoundEmailException} 객체
     * @return String : message
     */
    @ExceptionHandler(NotFoundEmailException.class)
    public String notFoundEmailExceiptionHandler(NotFoundEmailException e) {
        log.error("NotFoundEmailException");
        log.error("Status : {}", HttpStatus.BAD_REQUEST);
        log.error("Error Message : {}", e.getMessage());

        return e.getMessage();
    }

    /**
     * {@link NotFoundPasswordException} 예외 처리
     * @param e {@link NotFoundPasswordException} 객체
     * @return String : message
     */
    @ExceptionHandler(NotFoundPasswordException.class)
    public String notFoundPasswordExceiptionHandler(NotFoundPasswordException e) {
        log.error("Status : {}", HttpStatus.BAD_REQUEST);
        log.error("Error Message : {}", e.getMessage());

        return e.getMessage();
    }

    /**
     * {@link DuplicationEmailException} 예외 처리
     * @param e {@link DuplicationEmailException} 객체
     * @return String : message
     */
    @ExceptionHandler(DuplicationEmailException.class)
    public String duplicationEmailExceiptionHandler(DuplicationEmailException e) {
        log.error("Status : {}", HttpStatus.BAD_REQUEST);
        log.error("Error Message : {}", e.getMessage());

        return e.getMessage();
    }

    /**
     * {@link DuplicationUserNameException} 예외 처리
     * @param e {@link DuplicationUserNameException} 객체
     * @return String : message
     */
    @ExceptionHandler(DuplicationUserNameException.class)
    public String duplicationUserNameExceiptionHandler(DuplicationUserNameException e) {
        log.error("Status : {}", HttpStatus.BAD_REQUEST);
        log.error("Error Message : {}", e.getMessage());

        return e.getMessage();
    }
}
