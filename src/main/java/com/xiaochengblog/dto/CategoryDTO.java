package com.xiaochengblog.dto;

public record CategoryDTO(
    Long id,
    String name,
    String slug,
    String description,
    String color
) {}
