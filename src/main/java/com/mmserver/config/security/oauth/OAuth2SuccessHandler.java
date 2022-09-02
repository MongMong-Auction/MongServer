package com.mmserver.config.security.oauth;

import com.mmserver.config.security.UserInfo;
import com.mmserver.config.security.jwt.JwtProvider;
import com.mmserver.domain.model.Token;
import com.mmserver.domain.model.User;
import com.mmserver.repository.RedisRepository;
import com.mmserver.repository.UserRepository;
import com.mmserver.utils.CookieUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.mmserver.config.security.oauth.OAuth2AuthorizationRequestRepository.COOKIE_KEY;

/**
 * OAuth2 인증 성공 시, 후 처리를 위한 클래스
 * {@link AuthenticationSuccessHandler}을 구현
 */
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    /**
     * 사용자 데이터 관리 Repository
     */
    private final UserRepository userRepository;

    /**
     * Refresh Token을 관리 Repository
     */
    private final RedisRepository redisRepository;

    /**
     * JWT 토큰 관리 Component
     */
    private final JwtProvider jwtProvider;

    /**
     * 성공적으로 인증되면 호출
     * 전송받은 URL로 Redirect 작업 수행
     *     사용자 정보가 있는 경우 : 사용자 정보 수정
     *     사용자 정보가 없는 경우 : 사용자 정보 수정
     *
     * @param request        : 로그인 시, 요청 객체
     * @param response       : 응답 객체
     * @param authentication : 인증 프로세스에서 생성된 Authentication 객체
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        log.info("OAuth2 인증 성공");

        // 리다이렉트 객체 생성
        DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

        // Authentication 객체에서 OAuth2User 객체 추출
        UserInfo principal = (UserInfo) authentication.getPrincipal();
        // OAuth2User 객체에서 사용자 정보 추출
        User userInfo = principal.getUser();
        log.info("  userInfo                  => {}", userInfo);

        // DB에 저장된 정보가 있는지 확인
        // 있다면 조회된 정보로 세팅
        // 없다면 Authentication에 저장된 User 객체로 세팅
        User user = userRepository.findById(userInfo.getEmail())
                .orElse(userInfo);
        log.info("  findUser                  => {}", user);

        // 사용자 정보 업데이트
        user.mainInfoUpdate(user);
        // 마지막 로그인 날짜 변경
        user.lastLoginUpdate();

        // 사용자 정보 저장/수정
        userRepository.save(user);

        // 조회된 사용자 세팅
        principal.setUser(user);

        // JWT 발급
        Token accessToken  = jwtProvider.createAccessToken(principal);
        Token refreshToken = jwtProvider.createRefreshToken(principal);

        // 헤더에 토큰 값 세팅
        jwtProvider.setHeaderAccessToken(response, accessToken.getToken());

        // Redis에 Refesh Token 저장
        redisRepository.save(refreshToken);

        // Cookie에서 targetUri 추출
        String targetUri = getTargetUri(request, user);
        // targetUri로 리다이렉트
        redirectStrategy.sendRedirect(request, response, targetUri);
    }

    /**
     * Cookie에서 Redirect Uri 조회
     * Redirect Uri를 통해 Target Uri 세팅
     *
     * @param  request : 요청 객체
     * @param  user    : 사용자 정보
     * @return String  : Tager Uri
     */
    private String getTargetUri(HttpServletRequest request, User user) {
        // Cookie에서 Redirect Uri 추출
        String redirectUri = CookieUtils.getCookie(request, COOKIE_KEY).map(Cookie::getValue)
                .orElseThrow(() -> {
                    throw new RuntimeException("Redirect Uri가 존재하지 않습니다.");
                });

        log.info("  OAuth2 Login Redirect URI => {}", redirectUri);

        return UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("email", user.getEmail())
                .build().toUriString();
    }
}
