package com.xiaochengblog.controller;

import com.xiaochengblog.dto.ApiResponse;
import com.xiaochengblog.mapper.TagMapper;
import com.xiaochengblog.model.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagMapper tagMapper;

    @GetMapping
    public ApiResponse<List<Tag>> getAll() {
        return ApiResponse.success(tagMapper.selectList(null));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Tag> create(@RequestBody Map<String, Object> body) {
        Tag tag = Tag.builder()
                .name((String) body.get("name"))
                .slug((String) body.get("slug"))
                .build();
        tagMapper.insert(tag);
        return ApiResponse.success(tag);
    }
}
