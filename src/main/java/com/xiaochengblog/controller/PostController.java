package com.xiaochengblog.controller;

import com.xiaochengblog.dto.ApiResponse;
import com.xiaochengblog.dto.PaginatedResponse;
import com.xiaochengblog.dto.PostDTO;
import com.xiaochengblog.dto.PostRequest;
import com.xiaochengblog.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ApiResponse<PaginatedResponse<PostDTO>> getAllPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(postService.getAllPosts(page, size));
    }

    @GetMapping("/featured")
    public ApiResponse<List<PostDTO>> getFeaturedPosts() {
        return ApiResponse.success(postService.getFeaturedPosts());
    }

    @GetMapping("/search")
    public ApiResponse<PaginatedResponse<PostDTO>> searchPosts(
            @RequestParam("q") String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(postService.searchPosts(query, page, size));
    }

    @GetMapping("/{slug}")
    public ApiResponse<PostDTO> getPostBySlug(@PathVariable String slug) {
        return ApiResponse.success(postService.getPostBySlug(slug));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PostDTO> createPost(@Valid @RequestBody PostRequest request) {
        Long userId = getCurrentUserId();
        return ApiResponse.success(postService.createPost(request, userId));
    }

    @PutMapping("/{slug}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PostDTO> updatePost(@PathVariable String slug,
                                            @Valid @RequestBody PostRequest request) {
        Long userId = getCurrentUserId();
        return ApiResponse.success(postService.updatePost(slug, request, userId));
    }

    @DeleteMapping("/{slug}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deletePost(@PathVariable String slug) {
        Long userId = getCurrentUserId();
        postService.deletePost(slug, userId);
        return ApiResponse.success("删除成功", null);
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
