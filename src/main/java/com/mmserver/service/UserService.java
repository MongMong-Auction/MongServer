package com.mmserver.service;

import com.mmserver.config.security.UserInfo;
import com.mmserver.config.security.jwt.JwtProvider;
import com.mmserver.domain.LoginDTO;
import com.mmserver.domain.UserInfoDTO;
import com.mmserver.domain.model.Token;
import com.mmserver.domain.model.User;
import com.mmserver.exception.NotFoundEmailException;
import com.mmserver.exception.NotFoundPasswordException;
import com.mmserver.repository.RedisRepository;
import com.mmserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

/**
 * 사용자 관리 Service
 */
@Service
@RequiredArgsConstructor
public class UserService {

    /**
     * JWT 토큰 관리 Component
     */
    private final JwtProvider jwtProvider;

    /**
     * 사용자 데이터 관리 Repository
     */
    private final UserRepository userRepository;

    /**
     * Refresh Token을 관리 Repository
     */
    private final RedisRepository redisRepository;

    /**
     * 암호화 Encoder
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * 로그인
     *
     * @param  loginDTO    : 로그인 정보
     * @param  response    : 응답 객체
     * @return UserInfoDTO :
     */
    public UserInfoDTO login(LoginDTO loginDTO, HttpServletResponse response) {
        User user = userRepository.findById(loginDTO.getEmail())
                .orElseThrow(() -> {
                    throw new NotFoundEmailException();
                });
        if(!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())){
            throw new NotFoundPasswordException();
        }

        UserInfo userInfo = new UserInfo(user);

        Token accessToken = jwtProvider.createAccessToken(userInfo);
        Token refreshToken = jwtProvider.createRefreshToken(userInfo);

        jwtProvider.setHeaderAccessToken(response, accessToken.getToken());

        redisRepository.save(refreshToken);

        return new UserInfoDTO().toUserInfo(user);
    }
}
