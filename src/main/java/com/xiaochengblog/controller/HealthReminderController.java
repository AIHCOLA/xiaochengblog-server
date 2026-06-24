package com.xiaochengblog.controller;

import com.xiaochengblog.dto.ApiResponse;
import com.xiaochengblog.service.HealthReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/health-reminders")
@RequiredArgsConstructor
public class HealthReminderController {

    private final HealthReminderService service;

    @GetMapping
    public ApiResponse<Map<String, Boolean>> getStates() {
        Long userId = getCurrentUserId();
        return ApiResponse.success(service.getStates(userId));
    }

    @PutMapping("/{reminderId}")
    public ApiResponse<Map<String, Object>> setState(
            @PathVariable String reminderId,
            @RequestBody Map<String, Boolean> body) {
        Long userId = getCurrentUserId();
        return ApiResponse.success(service.setState(userId, reminderId, body.get("active")));
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
