package com.mmserver.service;

import com.mmserver.config.security.UserInfo;
import com.mmserver.config.security.jwt.JwtProvider;
import com.mmserver.domain.model.Token;
import com.mmserver.domain.model.User;
import com.mmserver.exception.NotFoundEmailException;
import com.mmserver.repository.RedisRepository;
import com.mmserver.repository.UserRepository;
import com.mmserver.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * 인증관리 Service
 */
@Service
@RequiredArgsConstructor
public class AuthService {

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
     * Access Token 재발급
     *
     * @param  request  : 요청 객체
     * @param  response : 응답 객체
     * @return boolean  : 재발급 성공여부
     */
    public boolean reissue(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 토큰 조회
            String token = jwtProvider.resolveToken(request);

            // 만료된 토큰을 통해 eamil 조회
            HashMap<String, String> payloadMap = JwtUtils.getPayloadByToken(token);

            // PayLoad 데이터 유무 확인
            if (payloadMap != null) {
                String email = payloadMap.get("sub");

                // 이메일을 통해 Redis에서 Refesh Token 조회
                Token refreshToken = redisRepository.findById(email)
                        .orElseThrow(() -> {
                            // 이메일에 해당하는 Key가 없는경우 예외 발생
                            throw new NotFoundEmailException();
                        });

                if (jwtProvider.validateToken(refreshToken.getToken())) {

                    // 로그인 아이디 통해 사용자 조회
                    User user = userRepository.findById(email)
                            .orElseThrow(() -> {
                                // 조회된 사용자 없는 경우 예외 발생
                                throw new NotFoundEmailException();
                            });

                    // 로그인 사용자 정보 인스턴스 생성
                    UserInfo userInfo = new UserInfo(user);

                    // Token 생성
                    Token newAccessToken = jwtProvider.createAccessToken(userInfo);
                    Token nweRefreshToken = jwtProvider.createRefreshToken(userInfo);

                    // Http Header에 Access Token세팅
                    jwtProvider.setHeaderAccessToken(response, newAccessToken.getToken());

                    // Refresh Token Redis에 저장
                    redisRepository.save(nweRefreshToken);

                    return true;
                }
            }
        } catch (ExpiredJwtException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }
}
