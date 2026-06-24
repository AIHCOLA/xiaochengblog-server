package com.xiaochengblog.security.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

public class WechatOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final DefaultOAuth2AuthorizationRequestResolver defaultResolver;

    public WechatOAuth2AuthorizationRequestResolver(ClientRegistrationRepository repo) {
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(repo, "/oauth2/authorization");
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest req = defaultResolver.resolve(request);
        return appendWechatRedirect(req);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String registrationId) {
        OAuth2AuthorizationRequest req = defaultResolver.resolve(request, registrationId);
        return appendWechatRedirect(req);
    }

    private OAuth2AuthorizationRequest appendWechatRedirect(OAuth2AuthorizationRequest req) {
        if (req == null || !"wechat".equals(req.getAttribute("registration_id"))) {
            return req;
        }
        return OAuth2AuthorizationRequest.from(req)
                .redirectUri(req.getRedirectUri() + "#wechat_redirect")
                .build();
    }
}
