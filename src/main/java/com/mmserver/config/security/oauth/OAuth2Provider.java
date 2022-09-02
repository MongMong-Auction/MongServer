package com.mmserver.config.security.oauth;

import com.mmserver.config.security.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * OAuth2 인증과정에서 Authentication 생성에 필요한 OAuth2User 객체를 만들기 위한 클래스
 */
@Component
@Slf4j
public class OAuth2Provider implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    /**
     * UserInfo Endpoint에서 최종 사용자의 사용자 정보를 가져옴
     * Authentication 생성을 위한 OAuth2User 객체를 반환
     *
     * @param  userRequest : 사용자 요청
     * @return OAuth2User  : 사용자 정보
     *                       (OAuth2User 구현체)
     * @throws OAuth2AuthenticationException : UserInfo Endpoint에서 사용자 특성을 가져오는 동안 오류가 발생한 경우
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("OAuth2 Load User");

        // OAuth2UserService 구현체 DefaultOAuth2UserService 사용
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();

        // OAuth 서비스에서 가져온 사용자 정보를 담고 있는 OAuth2User 인스턴스를 받음
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);
        // 사용자 정보 추출
        Map<String, Object> attributes = oAuth2User.getAttributes();
        log.info("  User Attributes => {}", attributes);

        // OAuth 서비스 이름
        String oauth = userRequest.getClientRegistration().getRegistrationId();
        log.info("  OAuth2          => {}", oauth);

        // OAuth 서비스의 유저 정보
        OAuth2UserInfo userInfo = OAuth2UserInfo.of(oauth, attributes);
        log.info("  userInfo        => {}", userInfo);

        // Kakao 로그인의 경우 email 값을 필수로 받을 수 없음
        // OAuth2 인증은 성공하였지만 email 정보 제공 미동의 경우
        // AccessToken을 통해 인증 정보 삭제 및 에러 메시지 출력
        if(userInfo.getEmail() == null){
            // 인증정보 삭제 여부
            if(kakaoLogout(userRequest.getAccessToken().getTokenValue())){
                throw new OAuth2AuthenticationException(new OAuth2Error("400"), "제공 항목 미동의 시, 계속 진행할 수 없습니다.");
            }else{
                log.info("인증정보 삭제 실패");
                log.info("OAuth 기관    => {}", oauth);
                log.info("Access Token => {}", userRequest.getAccessToken().getTokenValue());
                throw new RuntimeException("인증정보 삭제 실패");
            }
        }

        return new UserInfo(userInfo, attributes);
    }

    /**
     * Kakao 계정 로그아웃
     * Access Token을 통해 사용자 Access Token과 Refresh Token 만료 시킴
     *
     * @param  accessToken : OAuth 기관으로부터 발급받은 Access Token
     * @return boolean     : 통신 결과
     */
    private boolean kakaoLogout(String accessToken) {
        log.info("Kakao Logout");
        log.info("  Kakao Access Token => {}", accessToken);

        int result = 0;

        String host = "https://kapi.kakao.com/v1/user/unlink";

        try {
            URL url = new URL(host);

            // 페이지 정보 가져오기
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 요청 정보 세팅
            connection.setRequestMethod(HttpMethod.POST.name());
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);

            // 요청 결과 HttpStatus 확인
            result = connection.getResponseCode();
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return result == HttpStatus.OK.value();
    }
}