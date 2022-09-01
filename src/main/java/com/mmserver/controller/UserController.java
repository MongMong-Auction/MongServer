package com.mmserver.controller;

import com.mmserver.domain.mapper.UserInfoMapping;
import com.mmserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 관리 Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    /**
     * 사용자 관리 Service
     */
    private final UserService userService;

    /**
     * 사용자 정보 조회
     *
     * @param  email           : 사용자 이메일
     * @return UserInfoMapping : 사용자 정보 Mapper
     */
    @GetMapping("/{email}")
    public UserInfoMapping findUserByEmail(@PathVariable String email) {
        return userService.findUserByEmail(email);
    }
}
