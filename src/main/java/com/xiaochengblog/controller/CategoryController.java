package com.xiaochengblog.controller;

import com.xiaochengblog.dto.ApiResponse;
import com.xiaochengblog.mapper.CategoryMapper;
import com.xiaochengblog.model.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryMapper categoryMapper;

    @GetMapping
    public ApiResponse<List<Category>> getAll() {
        return ApiResponse.success(categoryMapper.selectList(null));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Category> create(@RequestBody Map<String, Object> body) {
        Category category = Category.builder()
                .name((String) body.get("name"))
                .slug((String) body.get("slug"))
                .description((String) body.getOrDefault("description", ""))
                .color((String) body.getOrDefault("color", "#6c5ce7"))
                .build();
        categoryMapper.insert(category);
        return ApiResponse.success(category);
    }
}
