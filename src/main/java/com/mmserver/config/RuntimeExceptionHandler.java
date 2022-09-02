package com.mmserver.config;

import com.mmserver.exception.DuplicationEmailExceiption;
import com.mmserver.exception.DuplicationUserNameExceiption;
import com.mmserver.exception.NotFoundEmailException;
import com.mmserver.exception.NotFoundPasswordException;
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
public class RuntimeExceptionHandler {

    /**
     * {@link NotFoundEmailException} 예외 처리
     * @param e {@link NotFoundEmailException} 객체
     * @return String : message
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NotFoundEmailException.class)
    public String notFoundEmailExceiptionHandler(NotFoundEmailException e) {
        log.error("NotFoundEmailException");
        log.error("  Status        : {}", HttpStatus.BAD_REQUEST);
        log.error("  Error Message : {}", e.getMessage());

        return e.getMessage();
    }

    /**
     * {@link NotFoundPasswordException} 예외 처리
     * @param e {@link NotFoundPasswordException} 객체
     * @return String : message
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NotFoundPasswordException.class)
    public String notFoundPasswordExceiptionHandler(NotFoundPasswordException e) {
        log.error("Status        : {}", HttpStatus.BAD_REQUEST);
        log.error("Error Message : {}", e.getMessage());

        return e.getMessage();
    }

    /**
     * {@link DuplicationEmailExceiption} 예외 처리
     * @param e {@link DuplicationEmailExceiption} 객체
     * @return String : message
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DuplicationEmailExceiption.class)
    public String duplicationEmailExceiptionHandler(DuplicationEmailExceiption e) {
        log.error("Status        : {}", HttpStatus.BAD_REQUEST);
        log.error("Error Message : {}", e.getMessage());

        return e.getMessage();
    }

    /**
     * {@link DuplicationUserNameExceiption} 예외 처리
     * @param e {@link DuplicationUserNameExceiption} 객체
     * @return String : message
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DuplicationUserNameExceiption.class)
    public String duplicationUserNameExceiptionHandler(DuplicationUserNameExceiption e) {
        log.error("Status        : {}", HttpStatus.BAD_REQUEST);
        log.error("Error Message : {}", e.getMessage());

        return e.getMessage();
    }
}
