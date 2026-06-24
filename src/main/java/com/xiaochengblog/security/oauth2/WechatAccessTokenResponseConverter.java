package com.xiaochengblog.security.oauth2;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;

import java.util.HashMap;
import java.util.Map;

public class WechatAccessTokenResponseConverter
        implements Converter<Map<String, Object>, OAuth2AccessTokenResponse> {

    @Override
    public OAuth2AccessTokenResponse convert(Map<String, Object> source) {
        String accessToken = (String) source.get(OAuth2ParameterNames.ACCESS_TOKEN);
        long expiresIn = source.get("expires_in") instanceof Number n
                ? n.longValue() : 7200L;

        Map<String, Object> additional = new HashMap<>(source);
        additional.remove(OAuth2ParameterNames.ACCESS_TOKEN);
        additional.remove("expires_in");

        return OAuth2AccessTokenResponse.withToken(accessToken)
                .tokenType(OAuth2AccessToken.TokenType.BEARER)
                .expiresIn(expiresIn)
                .additionalParameters(additional)
                .build();
    }
}
