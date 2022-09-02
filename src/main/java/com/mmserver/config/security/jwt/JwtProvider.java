package com.mmserver.config.security.jwt;

import com.mmserver.config.security.UserAuthentication;
import com.mmserver.config.security.UserInfo;
import com.mmserver.domain.model.Token;
import com.mmserver.service.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

/**
 * JWT 토큰 관리
 * JWT 생성, 확인, 정보 추출, 유효성 확인
 */
@Component
@Slf4j
public class JwtProvider {

    /**
     * JWT Secret 값
     */
    private final Key secretKey;

    /**
     * 헤더에 담긴 JWT의 키 값
     */
    private final String jwtHeader;

    /**
     * JWT 인증 타입
     */
    private final String jwtPrefix;

    /**
     * Access Token 만료 시간(30분)
     */
//    private final long accessTokenExpireTime  = Duration.ofMinutes(30).toMillis();
    private final long accessTokenExpireTime  = Duration.ofMillis(30).toMillis();

    /**
     * Refresh Token 만료 시간(1주일)
     */
    private final long refreshTokenExpireTime = Duration.ofDays(7).toMillis();


    /**
     * 사용자 관리 Service
     */
    private final UserService userService;

    /**
     * 생성자
     *
     * @param secretKeyHash : JWT Signature에서 사용할 암호화 된 Secret 값
     * @param jwtPrefix     : JWT 인증 타입
     */
    public JwtProvider(@Value("${jwt.secretKeyHash}") String secretKeyHash,
                       @Value("${jwt.responseHeader}") String jwtHeader,
                       @Value("${jwt.prefix}") String jwtPrefix,
                       UserService userService) {
        // SecretKey 인스턴스
        this.secretKey = Keys.hmacShaKeyFor(secretKeyHash.getBytes());
        this.jwtHeader = jwtHeader;
        this.jwtPrefix = jwtPrefix;

        this.userService = userService;
    }

    /**
     * Access Token 생성
     *
     * @param  userProfile : Security Authentication에 저장된 정보
     * @return Token       : 토큰 정보
     */
    public Token createAccessToken(UserInfo userProfile) {
        log.info("Access Token 생성");
        return createToken(userProfile, accessTokenExpireTime);
    }

    /**
     * Refresh Token 생성
     *
     * @param  userInfo : Security Authentication에 저장된 정보
     * @return Token    : 토큰 정보
     */
    public Token createRefreshToken(UserInfo userInfo) {
        log.info("Refresh Token 생성");
        return createToken(userInfo, refreshTokenExpireTime);
    }

    /**
     * JWT 생성
     *
     * @param  userInfo        : 사용자 정보
     * @param  tokenExpireTime : 만료 기간
     * @return Token           : 토큰 정보
     */
    private Token createToken(UserInfo userInfo, long tokenExpireTime) {
        log.info("  userInfo         => {}", userInfo);

        // PayLoad 정보 세팅
        Claims claims = Jwts.claims().setSubject(userInfo.getName());
        claims.put("roles", userInfo.getAuthorities());

        Date now = new Date();
        // JWT 만료 시간
        Date tokenExpiresIn = new Date(now.getTime() + tokenExpireTime);

        // JWT 생성
        String token = Jwts.builder()
                .setClaims(claims)               // PayLoad 정보
                .setIssuedAt(now)                // JWT 발행 시간
                .setExpiration(tokenExpiresIn)   // JWT 만료 시간
                .signWith(secretKey)             // Signature 정보
                .compact();

        String pattern = "yyyy-M-dd hh:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        log.info("  token            => {}", token);
        log.info("  tokenExpiresTime => {}", simpleDateFormat.format(tokenExpiresIn));

        return Token.builder()
                .key(userInfo.getName())
                .token(token)
                .tokenExpireTime(tokenExpiresIn)
                .build();
    }

    /**
     * JWT 복호화를 통해 인증정보 조회
     *
     * @param token : JWT
     */
    public Authentication getAuthentication(String token) {
        log.info("Get Authentication");
        // 사용자 이메일 조회
        String email = getUserEmail(token);
        log.info("  email => {}", email);

        // Authentication에 저장하기 위한 객체
        UserInfo userInfo = (UserInfo) userService.loadUserByUsername(email);

        return new UserAuthentication(userInfo, token, userInfo.getAuthorities());
    }

    /**
     * JWT PayLoad에서 사용자 이메일 정보 조회
     *
     * @param  token  : JWT
     * @return String : 사용자 이메일
     */
    public String getUserEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * JWT 추출
     *
     * @param  request : 요청 객체
     * @return String  : JWT
     */
    public String resolveToken(HttpServletRequest request) {
        log.info("Resolve Token");

        // Http Header에서 토큰정보 추출
        String token = request.getHeader(jwtHeader);

        log.info("  token => {}", token);

        if(StringUtils.hasText(token) && token.startsWith(jwtPrefix)){
            return token.substring(7);
        }

        log.info("  token 추출 실패");

        return null;
    }

    /**
     * Jwt 유효성 검사
     *
     * @param  token   : JWT
     * @return boolean : 유효성 여부(true => 유효한 JWT)
     */
    public boolean validateToken(String token) {
        log.info("JWT Validation");
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            log.info("  Success");
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("  잘못된 JWT 서명입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("  지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("  JWT 토큰이 잘못되었습니다.");
        }

        return false;
    }

    /**
     * 응답 객체에 Access Token 세팅
     *
     * @param response    : 응답 객체
     * @param accessToken : 토큰 값
     */
    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
        log.info("Set Access Token in Header");
        log.info("  {} : {}", jwtHeader, jwtPrefix + " " + accessToken);
        response.setHeader(jwtHeader, jwtPrefix + " " + accessToken);
    }
}
