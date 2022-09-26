package com.mmserver.controller;

import com.mmserver.domain.UserInfoDTO;
import com.mmserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * @param  id          : 식별 값
     * @return UserInfoDTO : 사용자 정보
     */
    @GetMapping("/{id}")
    public UserInfoDTO findUserByEmail(@PathVariable Long id) {
        return userService.findUserByEmail(id);
    }
}
