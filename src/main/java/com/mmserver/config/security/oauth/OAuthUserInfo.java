package com.mmserver.config.security.oauth;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

/**
 * 사용자 정보 가공을 위한 객체
 */
@Getter
@ToString
public class OAuthUserInfo {

    /**
     * OAuth 사용자 정보
     */
    Map<String, Object> attributes;

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
    private OAuthUserInfo(String email, String oauth, String userName) {
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
    public static OAuthUserInfo of(String oauth, Map<String, Object> attributes){
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
    private static OAuthUserInfo ofGoogle(String oauth, Map<String, Object> attributes) {
        return OAuthUserInfo.builder()
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
    private static OAuthUserInfo ofKakao(String oauth, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuthUserInfo.builder()
                .oauth(oauth)
                .email((String) kakaoAccount.get("email"))
                .userName((String) kakaoProfile.get("nickname"))
                .build();
    }
}
