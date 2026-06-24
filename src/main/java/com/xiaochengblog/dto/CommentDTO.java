package com.xiaochengblog.dto;

import java.time.LocalDateTime;

public record CommentDTO(
    Long id,
    Long postId,
    Long parentId,
    String author,
    String email,
    String content,
    int likes,
    LocalDateTime createdAt
) {}
