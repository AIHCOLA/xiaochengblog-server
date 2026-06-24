package com.xiaochengblog.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record UserDTO(
    Long id,
    String username,
    String email,
    String avatar,
    String bio,
    String link,
    Map<String, String> socialLinks,
    String role,
    LocalDateTime createdAt
) {}
