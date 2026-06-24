package com.xiaochengblog.security;

import com.xiaochengblog.mapper.UserMapper;
import com.xiaochengblog.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserMapper userMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        String provider = userRequest.getClientRegistration().getRegistrationId();

        OAuth2User oAuth2User;
        if ("wechat".equals(provider)) {
            oAuth2User = loadWechatUser(userRequest);
        } else {
            oAuth2User = super.loadUser(userRequest);
        }

        Map<String, Object> attrs = oAuth2User.getAttributes();
        String providerId = extractProviderId(provider, attrs);
        String email = extractEmail(provider, attrs);
        String username = extractUsername(provider, attrs);
        String avatar = extractAvatar(provider, attrs);

        User user = findOrCreateUser(provider, providerId, email, username, avatar);
        return new CustomOAuth2User(oAuth2User, user);
    }

    private OAuth2User loadWechatUser(OAuth2UserRequest userRequest) {
        String accessToken = userRequest.getAccessToken().getTokenValue();
        String openid = com.xiaochengblog.config.WechatOAuth2Config.WECHAT_OPENID_HOLDER.get();
        try {
            String url = userRequest.getClientRegistration().getProviderDetails()
                    .getUserInfoEndpoint().getUri();
            URI uri = UriComponentsBuilder.fromUriString(url)
                    .queryParam("access_token", accessToken)
                    .queryParam("openid", openid)
                    .build().toUri();

            RequestEntity<Void> req = new RequestEntity<>(HttpMethod.GET, uri);
            Map<String, Object> attrs = restTemplate.exchange(
                    req, new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();

            if (attrs != null && openid != null) {
                attrs.put("openid", openid);
            }

            return new DefaultOAuth2User(
                    List.of(new SimpleGrantedAuthority("ROLE_USER")),
                    attrs != null ? attrs : Map.of(),
                    "openid");
        } finally {
            com.xiaochengblog.config.WechatOAuth2Config.WECHAT_OPENID_HOLDER.remove();
        }
    }

    private User findOrCreateUser(String provider, String providerId, String email, String username, String avatar) {
        User user = userMapper.findByOAuth2ProviderAndId(provider, providerId);
        if (user != null) return user;

        if (email != null && !email.isBlank()) {
            user = userMapper.findByEmail(email);
            if (user != null) {
                user.setOauth2Provider(provider);
                user.setOauth2ProviderId(providerId);
                if (user.getAvatar() == null && avatar != null) {
                    user.setAvatar(avatar);
                }
                userMapper.updateById(user);
                return user;
            }
        }

        user = User.builder()
                .username(username)
                .email(email != null && !email.isBlank() ? email : providerId + "@" + provider + ".oauth")
                .avatar(avatar)
                .role("USER")
                .oauth2Provider(provider)
                .oauth2ProviderId(providerId)
                .build();
        userMapper.insert(user);
        return user;
    }

    private String extractProviderId(String provider, Map<String, Object> attrs) {
        return switch (provider) {
            case "github" -> attrs.get("id") != null ? attrs.get("id").toString() : null;
            case "google" -> (String) attrs.get("sub");
            default -> (String) attrs.get("openid");
        };
    }

    private String extractEmail(String provider, Map<String, Object> attrs) {
        if ("github".equals(provider) && attrs.get("email") instanceof String e && !e.isBlank()) {
            return e;
        }
        if ("google".equals(provider) && attrs.get("email") instanceof String e) {
            return e;
        }
        return null;
    }

    private String extractUsername(String provider, Map<String, Object> attrs) {
        return switch (provider) {
            case "github" -> (String) attrs.get("login");
            case "google" -> (String) attrs.get("name");
            default -> (String) attrs.getOrDefault("nickname", "WeChat User");
        };
    }

    private String extractAvatar(String provider, Map<String, Object> attrs) {
        return switch (provider) {
            case "github" -> (String) attrs.get("avatar_url");
            case "google" -> (String) attrs.get("picture");
            default -> (String) attrs.get("headimgurl");
        };
    }
}
