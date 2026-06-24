package com.xiaochengblog.controller;

import com.xiaochengblog.dto.ApiResponse;
import com.xiaochengblog.dto.UserDTO;
import com.xiaochengblog.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<UserDTO> getProfile() {
        Long userId = getCurrentUserId();
        return ApiResponse.success(authService.getProfile(userId));
    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<UserDTO> updateProfile(@RequestBody Map<String, Object> updates) {
        Long userId = getCurrentUserId();
        return ApiResponse.success(authService.updateProfile(userId, updates));
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
