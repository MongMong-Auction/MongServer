package com.mmserver.controller;

import com.mmserver.domain.LoginDTO;
import com.mmserver.domain.SignupDTO;
import com.mmserver.domain.UserInfoDTO;
import com.mmserver.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 인증관리 Controller
 */
@RestController
@RequiredArgsConstructor
public class AuthController {

    /**
     * 인증관리 Service
     */
    private final AuthService authService;

    /**
     * Access Token 재발급
     *
     * @param  request  : 요청 객체
     * @param  response : 응답 객체
     * @return boolean  : 재발급 성공여부
     */
    @PostMapping("/reissue")
    public boolean reissue(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return authService.reissue(request, response);
    }

    /**
     * 회원가입
     * (회원가입 후 자동 로그인)
     *
     * @param  signInfo    : 회원가입 정보
     * @param  response    : 응답 객체
     * @return UserInfoDTO : 로그인 사용자 정보
     */
    @PostMapping("/signup")
    public UserInfoDTO signup(@RequestBody SignupDTO signInfo, HttpServletResponse response) {
        return authService.signup(signInfo, response);
    }

    /**
     * 로그인
     *
     * @param  loginDTO    : 로그인 정보
     * @param  response    : 응답 객체
     * @return UserInfoDTO : 로그인 사용자 정보
     */
    @GetMapping("/login")
    public UserInfoDTO login(@ModelAttribute LoginDTO loginDTO, HttpServletResponse response) {
        return authService.login(loginDTO, response);
    }
}
