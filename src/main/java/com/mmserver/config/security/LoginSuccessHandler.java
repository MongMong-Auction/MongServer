package com.mmserver.config.security;

import com.mmserver.domain.model.User;
import com.mmserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 인증 성공 시, 후 처리를 위한 클래스
 * {@link AuthenticationSuccessHandler}을 구현
 */
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    /**
     * 사용자 데이터 관리 Repository
     */
    private final UserRepository userRepository;

    /**
     * 성공적으로 인증되면 호출
     * 경우에 맞는 URL로 Redirect 작업 수행
     *      사용자 정보가 있는 경우 : 사용자 정보 수정
     *                            이전 페이지로 Redirect
     *      사용자 정보가 없는 경우 : 사용자 정보 수정
     *                            추가 정보 입력 페이지로 Redirect
     *
     * @param request        : 로그인 시, 요청
     * @param response       : 응답
     * @param authentication : 인증 프로세스에서 생성된 Authentication 객체
     * @throws IOException      : I/O에서 오류가 발생한 경우
     * @throws ServletException : 서블릿에서 오류가 발생한 경우
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // Authentication 객체에서 OAuth2User 객체 추출
        UserProfile principal = (UserProfile) authentication.getPrincipal();
        // OAuth2User 객체에서 사용자 정보 추출
        User userInfo = principal.getUser();

        // DB에 저장된 정보가 있는지 확인
        // 있다면 조회된 정보로 세팅
        // 없다면 Authentication에 저장된 User 객체로 세팅
        User user = userRepository.findById(userInfo.getEmail())
                .orElse(userInfo);

        // 사용자 정보 업데이트
        user.mainInfoUpdate(user);
        // 마지막 로그인 날짜 변경
        user.lastLoginUpdate();

        // 사용자 정보 저장/수정
        userRepository.save(user);

        // TODO : 1. JWT 발급
        //        2-1. 저장 안된 사용자 => 개인정보 수정 페이지로 Redirect
        //        3-2. 저장된 사용자 => 이전 페이지로 Redirect
    }
}
