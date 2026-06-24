package com.xiaochengblog.controller;

import com.xiaochengblog.dto.ApiResponse;
import com.xiaochengblog.dto.CommentRequest;
import com.xiaochengblog.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/posts/{postId}/comments")
    public ApiResponse<List<Map<String, Object>>> getComments(@PathVariable Long postId) {
        return ApiResponse.success(commentService.getCommentsByPostId(postId));
    }

    @PostMapping("/posts/{postId}/comments")
    public ApiResponse<Map<String, Object>> addComment(@PathVariable Long postId,
                                                        @Valid @RequestBody CommentRequest request) {
        return ApiResponse.success(commentService.addComment(postId, request));
    }

    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ApiResponse.success("删除成功", null);
    }

    @PostMapping("/comments/{commentId}/like")
    public ApiResponse<Map<String, Object>> likeComment(@PathVariable Long commentId) {
        return ApiResponse.success(commentService.likeComment(commentId));
    }
}
