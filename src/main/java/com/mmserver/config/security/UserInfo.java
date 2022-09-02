package com.mmserver.config.security;

import com.mmserver.config.security.oauth.OAuth2UserInfo;
import com.mmserver.domain.model.User;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Security Authentication에 저장하기 위한 객체
 */
@ToString
public class UserInfo implements UserDetails, OAuth2User {

    /**
     * 사용자 정보
     */
    private User user;

    /**
     * OAuth 기관으로부터 제공받은 사용자 정보
     */
    private Map<String, Object> attributes;

    /**
     * 로컬 로그인 사용을 위한 생성자
     *
     * @param user : 사용자 정보
     */
    public UserInfo(User user) {
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
     * 사용자 정보 접근자
     * (조회한 사용자 정보 세팅)
     *
     * @param user : 조회된 사용자 정보
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * 소셜 로그인 사용을 위한 생성자
     *
     * @param userInfo : OAuth 기관으로부터 얻은 사용자 정보
     */
    public UserInfo(OAuth2UserInfo userInfo, Map<String, Object> attributes) {
        this.user       = new User().oauthInfoUpdate(userInfo);
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
     * 사용자 인증을 위한 비밀번호
     *
     * @return String : 비밀번호
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * 사용자 인증을 위한 사용자 이름
     * (null을 반환할 수 없음)
     *
     * @return String : 사용자 이름
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * 사용자 계정 만료 여부
     *
     * @return boolean : 유효한 경우 => true
     *                   만료된 경우 => false
     */
    @Override
    public boolean isAccountNonExpired() {
        return user.getLockYn().equals("N");
    }

    /**
     * 사용자 계정 잠김 여부
     *
     * @return boolean : 잠기지 않은 경우 => true
     *                   잠긴 경우       => false
     */
    @Override
    public boolean isAccountNonLocked() {
        return user.getLockYn().equals("N");
    }

    /**
     * 사용자 비밀번호 만료 여부
     *
     * @return boolean : 유효한 경우 => true
     *                   만료된 경우 => false
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    /**
     * 사용자 계정 활성화 여부
     *
     * @return boolean : 활성화된 경우 => true
     *                   비활성화된 경우 => false
     */
    @Override
    public boolean isEnabled() {
        return user.getLockYn().equals("N");
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
