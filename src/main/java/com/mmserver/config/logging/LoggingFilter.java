package com.mmserver.config.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * Logging 필터
 */
@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

    /**
     * Http Request, Response Logging
     *
     * @see org.apache.catalina.AsyncDispatcher
     *
     * @param request     : 요청 객체
     * @param response    : 응답 객체
     * @param filterChain : 다음 필터를 호출하기 위한 객체
     * @throws ServletException : 서블릿에서 오류 발생한 경우
     * @throws IOException      : I/O 시, 오류가 발생한 경우
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(isAsyncDispatch(request)){
            filterChain.doFilter(request, response);
        }else{
            doFilterWrapper(new RequestWrapper(request), new ContentCachingResponseWrapper(response), filterChain);
        }
    }

    private void doFilterWrapper(HttpServletRequestWrapper request, ContentCachingResponseWrapper response, FilterChain filterChain) throws ServletException, IOException {
        try {
            logRequest(request);
            filterChain.doFilter(request, response);
        } finally {
            logResponse(response);
            response.copyBodyToResponse();
        }
    }

    private void logRequest(HttpServletRequestWrapper request) throws IOException {
        String queryString = request.getQueryString();

        log.info("Request : {} uri=[{}] content-type=[{}]",
                request.getMethod(),
                StringUtils.hasText(queryString) ? request.getRequestURI() + queryString : request.getRequestURI(),
                request.getContentType()
        );

        logPayLoad("Request", request.getInputStream());
    }

    private void logResponse(ContentCachingResponseWrapper response) throws IOException {
        logPayLoad("Response", response.getContentInputStream());
    }

    private void logPayLoad(String prefix, InputStream inputStream) throws IOException {
        byte[] content = StreamUtils.copyToByteArray(inputStream);
        if(content.length > 0){
            String contentString = new String(content);
            log.info("{} Body : {}", prefix, contentString);
        }
    }
}
