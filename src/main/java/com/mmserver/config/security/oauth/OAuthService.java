package com.mmserver.config.security.oauth;

import com.mmserver.config.security.UserProfile;
import com.mmserver.domain.model.User;
import com.mmserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * OAuth2 인증과정에서 Authentication 생성에 필요한 OAuth2User 객체를 만들기 위한 클래스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    /**
     * 사용자 데이터 관리 Repository
     */
    private final UserRepository userRepository;

    /**
     * UserInfo Endpoint에서 최종 사용자의 사용자 정보를 가져옴
     * Authentication 생성을 위한 OAuth2User 객체를 반환
     *
     * @param  userRequest : 사용자 요청
     * @return UserProfile : 사용자 정보
     *                       (OAuth2User 구현체)
     * @throws OAuth2AuthenticationException : UserInfo Endpoint에서 사용자 특성을 가져오는 동안 오류가 발생한 경우
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // OAuth2UserService 구현체 DefaultOAuth2UserService 사용
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();

        // OAuth 서비스에서 가져온 사용자 정보를 담고 있는 OAuth2User 인스턴스를 받음
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);
        // 사용자 정보 추출
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // OAuth 서비스 이름
        String oauth = userRequest.getClientRegistration().getRegistrationId();

        // OAuth 서비스의 유저 정보
        OAuthUserProfile userProfile = OAuthUserProfile.of(oauth, attributes);

        // OAuthUserProfile를 User로 변환 및 사용자 정보 저장
        User user = saveUser(userProfile);

        return new UserProfile(user, attributes);
    }

    /**
     * OAuthUserProfile 객체를 User 객체로 변환
     *
     * 사용자 정보 저장
     * 사용자 정보가 없는 경우 Insert
     * 사용자 경우가 없는 경우 Update
     *
     * @param  oAuthUserProfile : OAuth 기관으로부터 제공받은 데이터로 가공한 사용자 정보
     * @return user             : 사용자 정보
     */
    private User saveUser(OAuthUserProfile oAuthUserProfile) {
        // DB에 저장된 정보가 있는지 확인
        // 있다면 해당 정보 조회
        // 없다면 User 인스턴스 생성
        User user = userRepository.findById(oAuthUserProfile.getEmail())
                .orElseGet(User::new);

        // 제공받은 데이터 세팅
        user.mainInfoUpdate(oAuthUserProfile);

        // 데이터 저장 또는 업데이트
        userRepository.save(user);

        return user;
    }
}
