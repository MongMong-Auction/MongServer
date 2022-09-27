package com.mmserver.config.logging;

import org.springframework.util.StreamUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * HttpServletRequest Wrapper 클래스
 *
 *  - HttpServletRequest의 InputStream은 한번만 읽을 수 있기때문에
 *    캐싱해서 여러번 사용할 수 있도록 Wrapper 클래스 생성
 */
public class RequestWrapper extends HttpServletRequestWrapper {

    private final byte[] cachedInputStream;

    public RequestWrapper(HttpServletRequest request) throws IOException {
        super(request);

        try(InputStream requestInputStream = request.getInputStream()){
            cachedInputStream = StreamUtils.copyToByteArray(requestInputStream);
        }
    }

    @Override
    public ServletInputStream getInputStream() {
        return new CashedServledInputStream(cachedInputStream);
    }

    /**
     * Binary 데이터를 읽기위한 클래스
     * 일반적으로 ServletRequest.getInputStream을 통해 조회
     */
    private static class CashedServledInputStream extends ServletInputStream {

        private final ByteArrayInputStream buffer;

        public CashedServledInputStream(byte[] contents) {
            this.buffer = new ByteArrayInputStream(contents);
        }

        @Override
        public int read() {
            return buffer.read();
        }

        @Override
        public boolean isFinished() {
            return buffer.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {
            throw new UnsupportedOperationException("지원하지 않습니다.");
        }
    }
}
