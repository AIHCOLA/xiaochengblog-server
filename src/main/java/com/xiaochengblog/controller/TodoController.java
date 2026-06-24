package com.xiaochengblog.controller;

import com.xiaochengblog.dto.ApiResponse;
import com.xiaochengblog.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> getTodos() {
        Long userId = getCurrentUserId();
        return ApiResponse.success(todoService.getTodos(userId));
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> createTodo(@RequestBody Map<String, String> body) {
        Long userId = getCurrentUserId();
        return ApiResponse.success(todoService.createTodo(userId, body.get("text")));
    }

    @PutMapping("/{id}")
    public ApiResponse<Map<String, Object>> updateTodo(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        Long userId = getCurrentUserId();
        String text = body.containsKey("text") ? (String) body.get("text") : null;
        Boolean done = body.containsKey("done") ? (Boolean) body.get("done") : null;
        return ApiResponse.success(todoService.updateTodo(userId, id, text, done));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Map<String, Object>> deleteTodo(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        return ApiResponse.success(todoService.deleteTodo(userId, id));
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
