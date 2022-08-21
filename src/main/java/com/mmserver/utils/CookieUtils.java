package com.mmserver.utils;

import org.springframework.util.SerializationUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Optional;

/**
 * Cookie 관리 Util
 */
public class CookieUtils {

    /**
     * Cookie 값 조회
     *
     * @param  request          : 요청 객체
     * @param  name             : Cookie 이름
     * @return Optional<Cookie> : Cookie 조회 결과
     */
    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return Optional.of(cookie);
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Cookie 추가
     *
     * @param response : 응답 객체
     * @param name     : Cookie 이름
     * @param value    : Cookie 값
     * @param maxAge   : Cookie 사용 기간
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        // Cookie 세팅
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);

        // Cookie 추가
        response.addCookie(cookie);
    }

    /**
     * Cookie 삭제
     *
     * @param request  : 요청 객체
     * @param response : 응답 객체
     * @param name     : Cookie 이름
     */
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie: cookies) {
                if (cookie.getName().equals(name)) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }

    /**
     * Object 직렬화
     *
     * @param  object : 직렬화하기 위한 객체
     * @return String : 직렬화 결과
     */
    public static String serialize(Object object) {
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(object));
    }

    /**
     * Cookie 역직렬화
     *
     * @param  cookie : Cookie
     * @param  cls    : 역질렬화 캐스트하기 위한 제네릭 클래스
     * @return T      : 역직렬화 결과
     */
    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue())));
    }
}
