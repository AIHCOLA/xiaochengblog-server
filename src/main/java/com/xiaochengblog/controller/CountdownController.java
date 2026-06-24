package com.xiaochengblog.controller;

import com.xiaochengblog.dto.ApiResponse;
import com.xiaochengblog.service.CountdownService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/countdowns")
@RequiredArgsConstructor
public class CountdownController {

    private final CountdownService countdownService;

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> getCountdowns() {
        Long userId = getCurrentUserId();
        return ApiResponse.success(countdownService.getCountdowns(userId));
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> createCountdown(@RequestBody Map<String, String> body) {
        Long userId = getCurrentUserId();
        LocalDateTime targetDate = LocalDateTime.parse(body.get("targetDate"));
        return ApiResponse.success(countdownService.createCountdown(userId, body.get("name"), targetDate));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Map<String, Object>> deleteCountdown(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        return ApiResponse.success(countdownService.deleteCountdown(userId, id));
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
