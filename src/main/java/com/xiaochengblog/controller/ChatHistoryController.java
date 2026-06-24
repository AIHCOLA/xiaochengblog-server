package com.xiaochengblog.controller;

import com.xiaochengblog.dto.ApiResponse;
import com.xiaochengblog.service.ChatHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatHistoryController {

    private final ChatHistoryService chatHistoryService;

    @GetMapping("/history")
    public ApiResponse<List<Map<String, Object>>> getHistory() {
        Long userId = getCurrentUserId();
        return ApiResponse.success(chatHistoryService.getHistory(userId));
    }

    @PostMapping("/history")
    public ApiResponse<Map<String, Object>> saveMessage(@RequestBody Map<String, String> body) {
        Long userId = getCurrentUserId();
        return ApiResponse.success(chatHistoryService.saveMessage(userId, body.get("role"), body.get("content")));
    }

    @DeleteMapping("/history")
    public ApiResponse<Map<String, Object>> clearHistory() {
        Long userId = getCurrentUserId();
        chatHistoryService.clearHistory(userId);
        Map<String, Object> result = new java.util.LinkedHashMap<>();
        result.put("cleared", true);
        return ApiResponse.success(result);
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
