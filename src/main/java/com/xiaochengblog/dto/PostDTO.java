package com.xiaochengblog.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PostDTO(
    Long id,
    String title,
    String slug,
    String excerpt,
    String content,
    String coverImage,
    boolean featured,
    int likes,
    int views,
    int readingTime,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    CategoryDTO category,
    List<TagDTO> tags,
    AuthorDTO author
) {}
