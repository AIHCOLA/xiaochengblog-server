package com.xiaochengblog.dto;

import java.util.Map;

public record AuthorDTO(
    String name,
    String avatar,
    String bio,
    Map<String, String> links
) {}
