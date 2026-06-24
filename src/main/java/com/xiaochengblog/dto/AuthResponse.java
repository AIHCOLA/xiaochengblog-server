package com.xiaochengblog.dto;

public record AuthResponse(
    String token,
    String refreshToken,
    UserDTO user
) {}
