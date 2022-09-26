package com.mmserver.service;

import com.mmserver.config.security.UserInfo;
import com.mmserver.config.security.jwt.JwtProvider;
import com.mmserver.domain.LoginDTO;
import com.mmserver.domain.SignupDTO;
import com.mmserver.domain.UserInfoDTO;
import com.mmserver.domain.model.Token;
import com.mmserver.domain.model.User;
import com.mmserver.exception.DuplicationEmailException;
import com.mmserver.exception.DuplicationUserNameException;
import com.mmserver.exception.NotFoundEmailException;
import com.mmserver.exception.NotFoundPasswordException;
import com.mmserver.repository.RedisRepository;
import com.mmserver.repository.UserRepository;
import com.mmserver.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

import static com.mmserver.domain.EnumType.ErrorCode.JWT_ACCESS_TOKEN_EMPTY;
import static com.mmserver.domain.EnumType.ErrorCode.JWT_REFRESH_TOKEN_EXPIRED;

/**
 * 인증관리 Service
 */
@Slf4j
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
     * @throws IOException : I/O 시, 오류가 발생한 경우
     */
    public boolean reissue(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 토큰 조회
        String token = jwtProvider.resolveToken(request);
        log.info("token : {}", token);

        if(token == null) {
            log.error("토큰 값이 없습니다.");
            log.error("Status : {}", JWT_ACCESS_TOKEN_EMPTY.getStatus());
            log.error("Error Message : {}", JWT_ACCESS_TOKEN_EMPTY.getMsg());

            response.sendError(JWT_ACCESS_TOKEN_EMPTY.getStatus(), JWT_ACCESS_TOKEN_EMPTY.getMsg());

            return false;
        }

        try {
            // 만료된 토큰을 통해 eamil 조회
            HashMap<String, String> payloadMap = JwtUtils.getPayloadByToken(token);
            log.info("Access Token payloadMap : {}", payloadMap);

            // PayLoad 데이터 유무 확인
            if (payloadMap != null) {
                String subData = payloadMap.get("sub");
                log.info("Access Token Payload Email : {}", subData);

                // 이메일을 통해 Redis에서 Refesh Token 조회
                Token refreshToken = redisRepository.findById(subData)
                        .orElseThrow(() -> {
                            log.error("Redis에 저장된 정보 없음");
                            // 이메일에 해당하는 Key가 없는경우 예외 발생
                            throw new NotFoundEmailException();
                        });
                log.info("refreshToken : {}", refreshToken);

                if (StringUtils.hasText(subData) && jwtProvider.validateToken(refreshToken.getToken())) {
                    String email = jwtProvider.getUserEmail(refreshToken.getToken());

                    // 로그인 아이디 통해 사용자 조회
                    User user = userRepository.findByEmail(email)
                            .orElseThrow(() -> {
                                log.error("Refesh Token 정보로 조회되는 사용자 없음");
                                // 조회된 사용자 없는 경우 예외 발생
                                throw new NotFoundEmailException();
                            });
                    log.info("Refresh Token Find User    : {}", user);

                    // 인증정보 세팅
                    registerAuthorizatione(user, response);

                    return true;
                }
            }
        } catch (ExpiredJwtException e) {
            log.error("Refresh Token 만료");
            log.error("Status : {}", JWT_REFRESH_TOKEN_EXPIRED.getStatus());
            log.error("  Error Message : {}", JWT_REFRESH_TOKEN_EXPIRED.getMsg());

            response.sendError(JWT_REFRESH_TOKEN_EXPIRED.getStatus(), JWT_REFRESH_TOKEN_EXPIRED.getMsg());
        }

        return false;
    }

    /**
     * 회원가입
     * 사용자 정보 저장 시, JWT 값 세팅
     *
     * @param  signInfo    : 회원가입 정보
     * @param  response    : 응답 객체
     * @return UserInfoDTO : 로그인 사용자 정보
     */
    public UserInfoDTO signup(SignupDTO signInfo, HttpServletResponse response) {
        // 사용자 이메일 중복 확인
        if(isCheckedEmail(signInfo.getEmail())) {
            throw new DuplicationEmailException();
        }

        // 사용자 이름 중복 확인
        if(isCheckedUserName(signInfo.getUserName())) {
            throw new DuplicationUserNameException();
        }

        // 저장 객체 생성
        User user = User.builder()
                .email(signInfo.getEmail())
                .userName(signInfo.getUserName())
                .password(passwordEncoder.encode(signInfo.getPassword()))
                .build();

        log.info("Signup User Info : {}", user);

        // 마지막 로그인 날짜 변경
        user.lastLoginUpdate();
        // 사용자 정보 저장
        user = userRepository.save(user);

        // 인증정보 세팅
        registerAuthorizatione(user, response);

        return user.toUserInfo();
    }

    /**
     * 사용자 이메일 중복확인
     *
     * @param  email   : 사용자 이메일
     * @return boolean : 중복 여부(true => 중복)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public boolean isCheckedEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    /**
     * 사용자 이름 중복확인
     *
     * @param  userName : 사용자 이름
     * @return boolean  : 중복 여부(true => 중복)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public boolean isCheckedUserName(String userName) {
        return userRepository.findByUserName(userName).isPresent();
    }

    /**
     * 로그인
     *
     * @param  loginDTO    : 로그인 정보
     * @param  response    : 응답 객체
     * @return UserInfoDTO : 로그인 사용자 정보
     */
    public UserInfoDTO login(LoginDTO loginDTO, HttpServletResponse response) {
        // 로그인 아이디 통해 사용자 조회
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> {
                    // 조회된 사용자 없는 경우 예외 발생
                    throw new NotFoundEmailException();
                });

        log.info("Login User Info : {}", user);

        // 비밀번호 확인
        if(!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())){
            // 비밀번호 실패 횟수 증가
            user.addFailCnt();
            // 비밀번호 미일치 예외
            throw new NotFoundPasswordException();
        }

        // 마지막 로그인 날짜 변경
        user.lastLoginUpdate();
        // 비밀번호 실패 횟수 초기화
        user.initFailCnt();

        // 인증정보 세팅
        registerAuthorizatione(user, response);

        return user.toUserInfo();
    }

    /**
     * 인증정보 세팅
     *      Access Token  -> Response Header에 세팅
     *      Refresh Token -> Redis에 저장
     *
     * @param user     : 사용자 정보
     * @param response : 응답 객체
     */
    private void registerAuthorizatione(User user, HttpServletResponse response) {
        // 로그인 사용자 정보 인스턴스 생성
        UserInfo userInfo = new UserInfo(user);

        // Token 생성
        Token accessToken  = jwtProvider.createAccessToken(userInfo);
        Token refreshToken = jwtProvider.createRefreshToken(userInfo);

        // Http Header에 Access Token세팅
        jwtProvider.setHeaderAccessToken(response, accessToken.getToken());

        // Refresh Token Redis에 저장
        redisRepository.save(refreshToken);
    }
}
