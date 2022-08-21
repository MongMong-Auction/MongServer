package com.mmserver.controller;

import com.mmserver.domain.LoginDTO;
import com.mmserver.domain.UserInfoDTO;
import com.mmserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * 사용자 관리 Controller
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    /**
     * 사용자 관리 Service
     */
    private final UserService userService;

    /**
     * 로그인
     *
     * @param  loginDTO : 로그인 정보
     * @param  response : 응답 객체
     * @return
     */
    @GetMapping("/login")
    public UserInfoDTO login(@ModelAttribute LoginDTO loginDTO, HttpServletResponse response){
        return userService.login(loginDTO, response);
    }
}
