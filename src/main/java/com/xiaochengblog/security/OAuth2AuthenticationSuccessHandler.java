package com.xiaochengblog.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Value("${app.oauth2.frontend-redirect-uri}")
    private String frontendRedirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();
        var appUser = principal.getAppUser();

        String token = jwtUtil.generateToken(appUser.getId(), appUser.getEmail(), appUser.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(appUser.getId());

        // Pass tokens via URL query params so they survive the cross-origin redirect.
        // The frontend will exchange them for httpOnly cookies via a Vite-proxied API call.
        String redirectUrl = UriComponentsBuilder.fromUriString(frontendRedirectUri)
                .queryParam("access_token", token)
                .queryParam("refresh_token", refreshToken)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
