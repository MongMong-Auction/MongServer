package com.mmserver.controller;

import com.mmserver.domain.LoginDto;
import com.mmserver.domain.SignupDto;
import com.mmserver.domain.UserInfoDto;
import com.mmserver.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * @param  signInfo                    : 회원가입 정보
     * @param  response                    : 응답 객체
     * @return ResponseEntity<UserInfoDto> : 로그인 사용자 정보
     */
    @PostMapping("/signup")
    public ResponseEntity<UserInfoDto> signup(@RequestBody SignupDto signInfo, HttpServletResponse response) {
        return new ResponseEntity<>(authService.signup(signInfo, response), HttpStatus.CREATED);
    }

    /**
     * 사용자 이메일 중복확인
     *
     * @param  email   : 사용자 이메일
     * @return boolean : 중복 여부(true => 중복)
     */
    @GetMapping("/check/email")
    public boolean checkedEmail(@RequestParam String email) {
        return authService.isCheckedEmail(email);
    }

    /**
     * 사용자 이름 중복확인
     *
     * @param  userName : 사용자 이름
     * @return boolean  : 중복 여부(true => 중복)
     */
    @GetMapping("/check/userName")
    public boolean checkedUserName(@RequestParam String userName) {
        return authService.isCheckedUserName(userName);
    }

    /**
     * 로그인
     *
     * @param  loginDTO    : 로그인 정보
     * @param  response    : 응답 객체
     * @return UserInfoDTO : 로그인 사용자 정보
     */
    @GetMapping("/login")
    public UserInfoDto login(@ModelAttribute LoginDto loginDTO, HttpServletResponse response) {
        return authService.login(loginDTO, response);
    }

    /**
     * 로그아웃
     *
     * @param email : 사용자 이메일
     */
    @DeleteMapping("/logout")
    public void logout(@RequestParam String email) {
        authService.logout(email);
    }
}
