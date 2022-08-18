package com.mmserver.config.security;

import com.mmserver.config.security.oauth.OAuthFailureHandler;
import com.mmserver.config.security.oauth.OAuthSuccessHandler;
import com.mmserver.config.security.oauth.OAuthProvider;
import com.mmserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security 환경 세팅
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    /**
     * 사용자 데이터 관리 Repository
     */
    private final UserRepository userRepository;

    /**
     *  OAuth2User 객체를 만들기 위한 Service
     */
    private final OAuthProvider oAuthProvider;

    /**
     * PasswordEncoder 구현체 설정
     *
     * @return BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * Security 설정
     *
     * @param http : {@link HttpSecurity}
     *               http 요청에 대한 웹 기반 보안을 구성하기 위한 객체
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                // CSRF 설정 Disable
                .csrf().disable()
                // Security는 세션을 사용하지만, REST API기 때문에 세션 설정 Stateless로 설정 => JWT 사용
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // URL 별 권한 설정
        http.authorizeRequests()
                .anyRequest().permitAll();

        // OAuth2 로그인 설정
        http.oauth2Login()
                // OAuth2를 통해 Authentication 생성에 필요한 OAuthUser 반환하는 클래스 지정
                .userInfoEndpoint().userService(oAuthProvider)
                .and()
                // 인증 성공 시, 호출하는 핸들러
                .successHandler(new OAuthSuccessHandler(userRepository))
                // 인증 실패 시, 호출하는 핸들러
                .failureHandler(new OAuthFailureHandler());

        return http.build();
    }
}
