package com.xiaochengblog.controller;

import com.xiaochengblog.dto.ApiResponse;
import com.xiaochengblog.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> getHistory() {
        Long userId = getCurrentUserId();
        return ApiResponse.success(historyService.getHistory(userId));
    }

    @PostMapping("/{postId}")
    public ApiResponse<Void> addToHistory(@PathVariable Long postId) {
        Long userId = getCurrentUserId();
        historyService.addToHistory(userId, postId);
        return ApiResponse.success("已添加到阅读历史", null);
    }

    @DeleteMapping
    public ApiResponse<Void> clearHistory() {
        Long userId = getCurrentUserId();
        historyService.clearHistory(userId);
        return ApiResponse.success("阅读历史已清空", null);
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
