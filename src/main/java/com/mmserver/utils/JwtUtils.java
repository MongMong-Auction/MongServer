package com.mmserver.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import java.util.HashMap;

/**
 * JWT 관리 Util
 */
@Slf4j
public class JwtUtils {

    /**
     * JWT 에서 PayLoad 정보 추출
     *
     * @param  token   : JWT
     * @return HashMap :
     */
    @SuppressWarnings("unchecked")
    public static HashMap<String, String> getPayloadByToken(String token) {
        try {
            String[] splitJwt = token.split("\\.");

            Base64.Decoder decoder = Base64.getDecoder();
            String payload = new String(decoder.decode(splitJwt[1].getBytes()));

            return new ObjectMapper().readValue(payload, HashMap.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
