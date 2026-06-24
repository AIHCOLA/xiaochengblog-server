package com.xiaochengblog.controller;

import com.xiaochengblog.dto.ApiResponse;
import com.xiaochengblog.service.StickyNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/sticky-note")
@RequiredArgsConstructor
public class StickyNoteController {

    private final StickyNoteService service;

    @GetMapping
    public ApiResponse<Map<String, Object>> get() {
        Long userId = getCurrentUserId();
        return ApiResponse.success(service.get(userId));
    }

    @PutMapping
    public ApiResponse<Map<String, Object>> save(@RequestBody Map<String, Object> body) {
        Long userId = getCurrentUserId();
        String content = body.containsKey("content") ? (String) body.get("content") : null;
        Integer colorIndex = body.containsKey("colorIndex") ? (Integer) body.get("colorIndex") : null;
        return ApiResponse.success(service.save(userId, content, colorIndex));
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
