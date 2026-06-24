package com.xiaochengblog.controller;

import com.xiaochengblog.dto.ApiResponse;
import com.xiaochengblog.service.MusicPlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/music-playlist")
@RequiredArgsConstructor
public class MusicPlaylistController {

    private final MusicPlaylistService musicPlaylistService;

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> getPlaylist() {
        Long userId = getCurrentUserId();
        return ApiResponse.success(musicPlaylistService.getPlaylist(userId));
    }

    @PutMapping
    public ApiResponse<List<Map<String, Object>>> savePlaylist(@RequestBody Map<String, Object> body) {
        Long userId = getCurrentUserId();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> songs = (List<Map<String, Object>>) body.get("songs");
        return ApiResponse.success(musicPlaylistService.savePlaylist(userId, songs));
    }

    @DeleteMapping
    public ApiResponse<Map<String, Object>> clearPlaylist() {
        Long userId = getCurrentUserId();
        return ApiResponse.success(musicPlaylistService.clearPlaylist(userId));
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
