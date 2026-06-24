package com.xiaochengblog.config;

import com.xiaochengblog.security.oauth2.WechatAccessTokenResponseConverter;
import com.xiaochengblog.security.oauth2.WechatOAuth2AuthorizationRequestResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Configuration
public class WechatOAuth2Config {

    private final WechatAccessTokenResponseConverter converter = new WechatAccessTokenResponseConverter();

    /** ThreadLocal to pass WeChat openid from token response to OAuth2UserService */
    public static final ThreadLocal<String> WECHAT_OPENID_HOLDER = new ThreadLocal<>();

    @Bean
    public OAuth2AuthorizationRequestResolver authorizationRequestResolver(
            ClientRegistrationRepository repo) {
        return new WechatOAuth2AuthorizationRequestResolver(repo);
    }

    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest>
            accessTokenResponseClient() {
        DefaultAuthorizationCodeTokenResponseClient defaultClient =
                new DefaultAuthorizationCodeTokenResponseClient();
        RestTemplate wechatRestTemplate = new RestTemplate();

        return (request) -> {
            String regId = request.getClientRegistration().getRegistrationId();
            if (!"wechat".equals(regId)) {
                return defaultClient.getTokenResponse(request);
            }

            var reg = request.getClientRegistration();
            var params = new LinkedMultiValueMap<String, String>();
            params.add("appid", reg.getClientId());
            params.add("secret", reg.getClientSecret());
            params.add(OAuth2ParameterNames.CODE, request.getAuthorizationExchange()
                    .getAuthorizationResponse().getCode());
            params.add(OAuth2ParameterNames.GRANT_TYPE, "authorization_code");

            URI uri = URI.create(reg.getProviderDetails().getTokenUri());
            uri = UriComponentsBuilder.fromUri(uri).queryParams(params).build().toUri();

            RequestEntity<Void> req = new RequestEntity<>(HttpMethod.GET, uri);
            Map<String, Object> body = wechatRestTemplate.exchange(
                    req, new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();

            // Store openid in ThreadLocal so CustomOAuth2UserService can access it
            if (body != null && body.get("openid") instanceof String oid) {
                WECHAT_OPENID_HOLDER.set(oid);
            }

            return converter.convert(body);
        };
    }
}
