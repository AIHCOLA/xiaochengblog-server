package com.xiaochengblog.controller;

import com.xiaochengblog.dto.ApiResponse;
import com.xiaochengblog.service.QuickLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quick-links")
@RequiredArgsConstructor
public class QuickLinkController {

    private final QuickLinkService service;

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> getAll() {
        Long userId = getCurrentUserId();
        return ApiResponse.success(service.getAll(userId));
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> create(@RequestBody Map<String, String> body) {
        Long userId = getCurrentUserId();
        return ApiResponse.success(service.create(userId, body.get("name"), body.get("url"), body.get("icon")));
    }

    @PutMapping("/{id}")
    public ApiResponse<Map<String, Object>> update(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Long userId = getCurrentUserId();
        return ApiResponse.success(service.update(userId, id, body.get("name"), body.get("url"), body.get("icon")));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Map<String, Object>> delete(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        return ApiResponse.success(service.delete(userId, id));
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
