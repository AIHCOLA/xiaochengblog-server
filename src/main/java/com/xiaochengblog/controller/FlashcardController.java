package com.xiaochengblog.controller;

import com.xiaochengblog.dto.ApiResponse;
import com.xiaochengblog.service.FlashcardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/flashcards")
@RequiredArgsConstructor
public class FlashcardController {

    private final FlashcardService flashcardService;

    @GetMapping("/known")
    public ApiResponse<List<Integer>> getKnownWordIds() {
        Long userId = getCurrentUserId();
        return ApiResponse.success(flashcardService.getKnownWordIds(userId));
    }

    @PostMapping("/known/{wordId}")
    public ApiResponse<Map<String, Object>> markKnown(@PathVariable Integer wordId) {
        Long userId = getCurrentUserId();
        return ApiResponse.success(flashcardService.markKnown(userId, wordId));
    }

    @DeleteMapping("/known/{wordId}")
    public ApiResponse<Map<String, Object>> markUnknown(@PathVariable Integer wordId) {
        Long userId = getCurrentUserId();
        return ApiResponse.success(flashcardService.markUnknown(userId, wordId));
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
