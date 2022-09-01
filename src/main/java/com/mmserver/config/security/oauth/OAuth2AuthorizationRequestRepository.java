package com.mmserver.config.security.oauth;

import com.mmserver.utils.CookieUtils;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 권한을 부여하기 전 처리를 위한 클래스
 * OAuth2LoginAuthenticationFillter에서 사용
 */
public class OAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    /**
     * 요청 객체 Cookie 키 값
     */
    public static final String REQUEST_COOKIE_KEY = "oauth2_auth_request";

    /**
     * Cookie 키 값
     */
    public static final String COOKIE_KEY = "redirect_uri";

    /**
     * 요청 객체에 포함된 OAuth2AuthorizationRequest 반환 메서드
     *
     * @param  request                    : 요청 객체
     * @return OAuth2AuthorizationRequest : OAuth2AuthorizationRequest 객체
     *                                      (사용할 수 없는 경우 null 반환)
     */
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return CookieUtils.getCookie(request, REQUEST_COOKIE_KEY)
                .map(cookie -> CookieUtils.deserialize(cookie, OAuth2AuthorizationRequest.class))
                .orElse(null);
    }

    /**
     * 요청에 포함된 OAuth2AuthorizationRequest 객체를 Cookie에 저장
     *
     * @param authorizationRequest : 요청에 포함된 OAuth2AuthorizationRequest 객체
     * @param request              : 요청 객체
     * @param response             : 응답 객체
     */
    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        // 사용여부 확인
        if (authorizationRequest == null) {
            removeAuthorizationRequest(request, response);
            return;
        }

        // Cookie 만료 시간(1분)
        int cookieExpireSeconds = 60;

        // Cookie에 OAuth2AuthorizationRequest 객체 추가
        CookieUtils.addCookie(response, REQUEST_COOKIE_KEY, CookieUtils.serialize(authorizationRequest), cookieExpireSeconds);
        // 요청객체에서  Redirect URI 추출
        String redirectUriAfterLogin = request.getParameter(COOKIE_KEY);

        if (StringUtils.isNotBlank(redirectUriAfterLogin)) {
            // Cookie에 Redirect URI 추가
            CookieUtils.addCookie(response, COOKIE_KEY, redirectUriAfterLogin, cookieExpireSeconds);
        }
    }

    /**
     * 요청 객체에 포함된 OAuth2AuthorizationRequest 반환
     *
     * @param  request                    : 요청 객체
     * @return OAuth2AuthorizationRequest : 요청 객체에서 제거된 OAuth2AuthorizationRequest
     *                                      (사용할 수 없는 경우 null 반환)
     */
    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
        return this.loadAuthorizationRequest(request);
    }
}
