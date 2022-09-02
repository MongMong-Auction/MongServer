package com.mmserver.config.security.oauth;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 사용자 정보 가공을 위한 객체
 */
@Slf4j
@Getter
@ToString
public class OAuth2UserInfo {

    /**
     * 사용자 이메일(Primary Key)
     */
    private final String email;

    /**
     * OAuth 제공 기관
     */
    private final String oauth;

    /**
     * 사용자 이름
     */
    private final String userName;

    /**
     * OAuthUserProfile 인스턴스를 만들기위한 생성자
     * 정적 팩터리사용을 위해 접근제어자 private 설정
     *
     * @param email    : 사용자 이메일
     * @param oauth    : OAuth 제공 기관
     * @param userName : 사용자 이름
     */
    @Builder(access = AccessLevel.PRIVATE)
    private OAuth2UserInfo(String email, String oauth, String userName) {
        this.email    = email;
        this.oauth    = oauth;
        this.userName = userName;
    }

    /**
     * OAuthUserProfile 객체 반환 정적 팩터리
     * OAuth 제공 기관에 따라 데이터 세팅
     *
     * @param  oauth            : OAuth 제공 기관
     * @param  attributes       : 사용자 정보를 담고있는 컬렉션
     * @return OAuthUserProfile : 가공된 사용자 정보
     */
    public static OAuth2UserInfo of(String oauth, Map<String, Object> attributes){
        log.info("Create {} OAuth2UserInfo Instance", oauth);
        switch(oauth){
            case "google":
                return ofGoogle(oauth, attributes);
            case "kakao":
                return ofKakao(oauth, attributes);
            default:
                throw new RuntimeException();
        }
    }

    /**
     * Google로부터 받은 사용자 정보를 통해
     * 데이터 세팅 후 OAuthUserProfile 인스턴스 반환
     *
     * @param  oauth            : OAuth 제공 기관
     * @param  attributes       : 사용자 정보를 담고있는 컬렉션
     * @return OAuthUserProfile : 가공된 사용자 정보
     */
    private static OAuth2UserInfo ofGoogle(String oauth, Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
                .oauth(oauth)
                .email((String) attributes.get("email"))
                .userName((String) attributes.get("name"))
                .build();
    }

    /**
     * Kakao로부터 받은 사용자 정보를 통해
     * 데이터 세팅 후 OAuthUserProfile 인스턴스 반환
     *
     * @param  oauth            : OAuth 제공 기관
     * @param  attributes       : 사용자 정보를 담고있는 컬렉션
     * @return OAuthUserProfile : 가공된 사용자 정보
     */
    @SuppressWarnings("unchecked")
    private static OAuth2UserInfo ofKakao(String oauth, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuth2UserInfo.builder()
                .oauth(oauth)
                .email((String) kakaoAccount.get("email"))
                .userName((String) kakaoProfile.get("nickname"))
                .build();
    }
}
