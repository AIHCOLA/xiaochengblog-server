package com.xiaochengblog.controller;

import com.xiaochengblog.dto.ApiResponse;
import com.xiaochengblog.service.MoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/moods")
@RequiredArgsConstructor
public class MoodController {

    private final MoodService moodService;

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> getMoods(
            @RequestParam(defaultValue = "0") int year,
            @RequestParam(defaultValue = "0") int month) {
        Long userId = getCurrentUserId();

        if (year > 0 && month > 0) {
            return ApiResponse.success(moodService.getMoods(userId, year, month));
        }
        return ApiResponse.success(moodService.getAllMoods(userId));
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> upsertMood(@RequestBody Map<String, Object> body) {
        Long userId = getCurrentUserId();
        LocalDate date = LocalDate.parse((String) body.get("date"));
        String emoji = (String) body.get("emoji");
        Boolean checkedIn = body.containsKey("checkedIn") ? (Boolean) body.get("checkedIn") : null;

        return ApiResponse.success(moodService.upsertMood(userId, date, emoji, checkedIn));
    }

    @DeleteMapping("/{date}")
    public ApiResponse<Map<String, Object>> deleteMood(@PathVariable String date) {
        Long userId = getCurrentUserId();
        LocalDate parsedDate = LocalDate.parse(date);
        return ApiResponse.success(moodService.deleteMood(userId, parsedDate));
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
