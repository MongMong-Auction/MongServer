package com.mmserver.config.security;

import com.mmserver.config.security.jwt.JwtAccessDeniedHandler;
import com.mmserver.config.security.jwt.JwtAuthenticationEntryPoint;
import com.mmserver.config.security.jwt.JwtAuthenticationFilter;
import com.mmserver.config.security.jwt.JwtProvider;
import com.mmserver.config.security.oauth.OAuth2AuthorizationRequestRepository;
import com.mmserver.config.security.oauth.OAuth2FailureHandler;
import com.mmserver.config.security.oauth.OAuth2Provider;
import com.mmserver.config.security.oauth.OAuth2SuccessHandler;
import com.mmserver.repository.RedisRepository;
import com.mmserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
     * Refresh Token을 관리 Repository
     */
    private final RedisRepository redisRepository;

    /**
     *  OAuth2User 객체를 만들기 위한 Service
     */
    private final OAuth2Provider oAuthProvider;

    /**
     * JWT 관리 Component
     */
    private final JwtProvider jwtProvider;

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
                // 기본 로그인 창 사용 안함
                .httpBasic().disable()
                // Restfull API 방식 사용으로 CSRF 설정 Disable
                // Cors 설정
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .csrf().disable()
                // Restfull API 방식 사용으로 formLogin 설정 Disable
                .formLogin().disable()
                // Security는 세션을 사용하지만, Restfull API 방식 사용으로 세션 설정 Stateless로 설정
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // URL 별 권한 설정
        http.authorizeRequests()
                .antMatchers(HttpMethod.GET, "/", "/login", "/check/**").permitAll()
                .antMatchers(HttpMethod.POST, "/signup", "/reissue").permitAll()
                .anyRequest().hasRole("USER");

        // JWT 설정
        // UsernamePasswordAuthenticationFilter 필터 전에 JwtAuthenticationFiler가 실행되도록 설정
        http.addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
                // 예외 처리 핸들링 설정
                .exceptionHandling()
                    // 인증예외 처리
                    .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                    // 인가예외 처리
                    .accessDeniedHandler(new JwtAccessDeniedHandler());

        // OAuth2 로그인 설정
        http.oauth2Login()
                // OAuth2AuthorizationRequest를 저장하기위해 필요한 레포지토리 설정
                .authorizationEndpoint().authorizationRequestRepository(new OAuth2AuthorizationRequestRepository())
                .and()
                // OAuth2를 통해 Authentication 생성에 필요한 OAuthUser 반환하는 클래스 지정
                .userInfoEndpoint().userService(oAuthProvider)
                .and()
                // 인증 성공 시, 호출하는 핸들러
                .successHandler(new OAuth2SuccessHandler(userRepository, redisRepository, jwtProvider))
                // 인증 실패 시, 호출하는 핸들러
                .failureHandler(new OAuth2FailureHandler());

        return http.build();
    }

    /**
     * Cors 설정
     *
     * @return CorsConfigurationSource : CorsConfiguataionSource 구현체 사용
     *                                   {@link UrlBasedCorsConfigurationSource}
     *                                   URL 경로 패턴을 통해 CorsConfiguraion을 선택
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // Cors 허용 설정
//        corsConfiguration.setAllowedOrigins(Arrays.asList("localhost:3000", "https://black-grass-0bd498c00.1.azurestaticapps.net"));
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();

        // 모든 url에 대해 적용
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        return urlBasedCorsConfigurationSource;
    }
}
