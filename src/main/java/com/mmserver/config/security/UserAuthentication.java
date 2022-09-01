package com.mmserver.config.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Authentication 구현체
 * {@link org.springframework.security.core.Authentication}
 */
public class UserAuthentication extends AbstractAuthenticationToken {

    /**
     * 주요 정보
     */
    private final Object principal;

    /**
     * 인증 정보
     */
    private final Object credentials;

    /**
     * 생성자
     *
     * @param principal   : 주요 정보
     * @param credentials : 인증 정보
     * @param authorities : 인가 정보
     */
    public UserAuthentication(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true); // must use super, as we override
    }

    /**
     * Credentials 접근자
     *
     * @return Object : 인증 정보
     */
    @Override
    public Object getCredentials() {
        return credentials;
    }

    /**
     * Principal 접근자
     *
     * @return Object : 주요 정보
     */
    @Override
    public Object getPrincipal() {
        return principal;
    }
}
