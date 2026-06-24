package com.xiaochengblog.controller;

import com.xiaochengblog.dto.ApiResponse;
import com.xiaochengblog.dto.GuestbookRequest;
import com.xiaochengblog.service.GuestbookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/guestbook")
@RequiredArgsConstructor
public class GuestbookController {

    private final GuestbookService guestbookService;

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> getAll() {
        return ApiResponse.success(guestbookService.getAllEntries());
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> add(@Valid @RequestBody GuestbookRequest request) {
        return ApiResponse.success(guestbookService.addEntry(request));
    }

    @DeleteMapping("/{entryId}")
    public ApiResponse<Void> delete(@PathVariable Long entryId) {
        guestbookService.deleteEntry(entryId);
        return ApiResponse.success("删除成功", null);
    }
}
