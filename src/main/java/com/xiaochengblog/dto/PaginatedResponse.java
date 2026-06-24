package com.xiaochengblog.dto;

import java.util.List;

public record PaginatedResponse<T>(
    List<T> items,
    long total,
    int page,
    int size,
    int totalPages
) {
    public static <T> PaginatedResponse<T> of(List<T> items, long total, int page, int size) {
        int totalPages = size > 0 ? (int) Math.ceil((double) total / size) : 0;
        return new PaginatedResponse<>(items, total, page, size, totalPages);
    }
}
