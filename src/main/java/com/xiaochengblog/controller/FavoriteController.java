package com.xiaochengblog.controller;

import com.xiaochengblog.dto.ApiResponse;
import com.xiaochengblog.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> getFavorites() {
        Long userId = getCurrentUserId();
        return ApiResponse.success(favoriteService.getFavorites(userId));
    }

    @PostMapping("/{postId}")
    public ApiResponse<Map<String, Object>> addFavorite(@PathVariable Long postId) {
        Long userId = getCurrentUserId();
        return ApiResponse.success(favoriteService.addFavorite(userId, postId));
    }

    @DeleteMapping("/{postId}")
    public ApiResponse<Map<String, Object>> removeFavorite(@PathVariable Long postId) {
        Long userId = getCurrentUserId();
        return ApiResponse.success(favoriteService.removeFavorite(userId, postId));
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
