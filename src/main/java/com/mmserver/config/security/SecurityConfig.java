package com.mmserver.config.security;

import com.mmserver.config.security.oauth.OAuthService;
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

    private final OAuthService oAuthService;

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

        return http.build();
    }
}
