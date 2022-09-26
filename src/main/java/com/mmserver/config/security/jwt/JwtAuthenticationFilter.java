package com.mmserver.config.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.mmserver.domain.EnumType.ErrorCode.JWT_ACCESS_TOKEN_EXPIRED;

/**
 * JWT 인증 필터
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * JWT 토큰 관리 Component
     */
    private final JwtProvider jwtProvider;


    /**
     * 필터 체인에 등록될 로직
     * (HttpServletRequest, HttpServletResponse 대신 제공)
     * @see org.apache.catalina.AsyncDispatcher
     *
     * @param request     : 요청 객체
     * @param response    : 응답 객체
     * @param filterChain :
     * @throws ServletException : 서블릿에서 오류 발생한 경우
     * @throws IOException      : I/O 시, 오류가 발생한 경우
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("JWT 인증 필터");
        try {
            String path = request.getServletPath();
            log.info("접근 Path : {}", path);

            // Access Token 재발급하는 경우 토큰 체크 안함
            if(!path.startsWith("/reissue")) {
                String accessToken = jwtProvider.resolveToken(request);
                log.info("Access Token : {}", accessToken);

                if (StringUtils.hasText(accessToken) && jwtProvider.validateToken(accessToken)) {
                    Authentication authentication = jwtProvider.getAuthentication(accessToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            log.error("Access Token 만료");
            log.error("Status : {}", JWT_ACCESS_TOKEN_EXPIRED.getStatus());
            log.error("Error Message : {}", JWT_ACCESS_TOKEN_EXPIRED.getMsg());

            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html; charset=UTF-8");
            response.setStatus(JWT_ACCESS_TOKEN_EXPIRED.getStatus());
            response.getWriter().write(JWT_ACCESS_TOKEN_EXPIRED.getMsg());
            response.getWriter().flush();
        }
    }
}