package com.xiaochengblog.controller;

import com.xiaochengblog.dto.ApiResponse;
import com.xiaochengblog.dto.AuthResponse;
import com.xiaochengblog.dto.LoginRequest;
import com.xiaochengblog.dto.RegisterRequest;
import com.xiaochengblog.dto.UserDTO;
import com.xiaochengblog.security.JwtUtil;
import com.xiaochengblog.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ApiResponse<UserDTO> login(@Valid @RequestBody LoginRequest request,
                                       HttpServletResponse response) {
        AuthResponse auth = authService.login(request);
        setJwtCookies(response, auth.token(), auth.refreshToken());
        return ApiResponse.success(auth.user());
    }

    @PostMapping("/register")
    public ApiResponse<UserDTO> register(@Valid @RequestBody RegisterRequest request,
                                          HttpServletResponse response) {
        AuthResponse auth = authService.register(request);
        setJwtCookies(response, auth.token(), auth.refreshToken());
        return ApiResponse.success(auth.user());
    }

    @PostMapping("/refresh")
    public ApiResponse<UserDTO> refresh(HttpServletRequest request,
                                         HttpServletResponse response) {
        String refreshToken = getCookieValue(request, "refresh_token");
        if (refreshToken == null) {
            return ApiResponse.error(401, "缺少刷新令牌");
        }
        try {
            AuthResponse auth = authService.refreshToken(refreshToken);
            setJwtCookies(response, auth.token(), auth.refreshToken());
            return ApiResponse.success(auth.user());
        } catch (Exception e) {
            clearJwtCookies(response);
            throw e;
        }
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletResponse response) {
        clearJwtCookies(response);
        return ApiResponse.success("已退出登录", null);
    }

    @GetMapping("/oauth2/authorize/{provider}")
    public void oauth2Authorize(@PathVariable String provider,
                                 HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/" + provider);
    }

    @PostMapping("/oauth/exchange")
    public ApiResponse<UserDTO> oauthExchange(@RequestBody Map<String, String> body,
                                              HttpServletResponse response) {
        String accessToken = body.get("access_token");
        String refreshToken = body.get("refresh_token");
        if (accessToken == null || !jwtUtil.validateToken(accessToken)) {
            return ApiResponse.error(401, "无效的令牌");
        }
        setJwtCookies(response, accessToken, refreshToken);
        Long userId = jwtUtil.getUserIdFromToken(accessToken);
        return ApiResponse.success(authService.getProfile(userId));
    }

    // ── Cookie helpers (public for access from security package) ──

    public static void setJwtCookies(HttpServletResponse response, String token, String refreshToken) {
        ResponseCookie accessCookie = ResponseCookie.from("access_token", token)
                .httpOnly(true)
                .secure(false) // set true in production with HTTPS
                .sameSite("Lax")
                .path("/")
                .maxAge(24 * 60 * 60) // 24h
                .build();
        response.addHeader("Set-Cookie", accessCookie.toString());

        if (refreshToken != null) {
            ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                    .httpOnly(true)
                    .secure(false)
                    .sameSite("Lax")
                    .path("/api/auth/refresh")
                    .maxAge(7 * 24 * 60 * 60) // 7d
                    .build();
            response.addHeader("Set-Cookie", refreshCookie.toString());
        }
    }

    public static void clearJwtCookies(HttpServletResponse response) {
        ResponseCookie accessCookie = ResponseCookie.from("access_token", "")
                .httpOnly(true).secure(false).sameSite("Lax").path("/").maxAge(0).build();
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true).secure(false).sameSite("Lax").path("/api/auth/refresh").maxAge(0).build();
        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
    }

    public static String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (Cookie c : request.getCookies()) {
            if (name.equals(c.getName())) return c.getValue();
        }
        return null;
    }
}
