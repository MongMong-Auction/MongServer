package com.mmserver.config.security;

import com.mmserver.domain.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Security Authentication에 저장하기 위한 객체
 */
public class UserProfile implements OAuth2User {

    /**
     * 사용자 정보
     */
    private final User user;

    /**
     * OAuth 기관으로부터 제공받은 사용자 정보
     */
    private Map<String, Object> attributes;

    /**
     * 로컬 로그인 사용을 위한 생성자
     *
     * @param user : 사용자 정보
     */
    public UserProfile(User user) {
        this.user = user;
    }

    /**
     * 사용자 정보 접근자
     *
     * @return User : 사용자 정보
     */
    public User getUser() {
        return user;
    }

    /**
     * 소셜 로그인 사용을 위한 생성자
     *
     * @param user : 사용자 정보
     */
    public UserProfile(User user, Map<String, Object> attributes) {
        this.user       = user;
        this.attributes = attributes;
    }

    /**
     * OAuth 기관으로 부터 받은 사용자 정보
     *
     * @return Map<String, Object> : 사용자 정보 컬렉션
     */
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * 권한 확인 조회
     * 하나의 사용자가 여러 역할을 가질 수 있기 때문에
     * 사용자 역할들을 반환
     *
     * @return Collection<GrantedAuthority> : 사용자 역할 목록
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> roles = new ArrayList<>();
        roles.add(() -> user.getRole().getValue());

        return roles;
    }

    /**
     * User의 Primary Key 반환
     *
     * @return : 사용자 이메일
     */
    @Override
    public String getName() {
        return user.getEmail();
    }
}
