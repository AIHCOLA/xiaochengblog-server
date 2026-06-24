package com.xiaochengblog.service;

import com.xiaochengblog.mapper.TodoMapper;
import com.xiaochengblog.model.Todo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoMapper todoMapper;

    public List<Map<String, Object>> getTodos(Long userId) {
        List<Todo> todos = todoMapper.selectByUserId(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Todo t : todos) {
            result.add(toMap(t));
        }
        return result;
    }

    @Transactional
    public Map<String, Object> createTodo(Long userId, String text) {
        Todo todo = Todo.builder()
                .userId(userId)
                .text(text)
                .done(false)
                .sortOrder(0)
                .build();
        todoMapper.insert(todo);
        return toMap(todo);
    }

    @Transactional
    public Map<String, Object> updateTodo(Long userId, Long todoId, String text, Boolean done) {
        Todo todo = todoMapper.selectById(todoId);
        if (todo == null || !todo.getUserId().equals(userId)) {
            throw new RuntimeException("Todo not found");
        }
        if (text != null) todo.setText(text);
        if (done != null) todo.setDone(done);
        todoMapper.updateById(todo);
        return toMap(todo);
    }

    @Transactional
    public Map<String, Object> deleteTodo(Long userId, Long todoId) {
        Todo todo = todoMapper.selectById(todoId);
        if (todo == null || !todo.getUserId().equals(userId)) {
            throw new RuntimeException("Todo not found");
        }
        todoMapper.deleteById(todoId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", todoId);
        result.put("deleted", true);
        return result;
    }

    private Map<String, Object> toMap(Todo t) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", t.getId());
        map.put("text", t.getText());
        map.put("done", t.getDone());
        map.put("sortOrder", t.getSortOrder());
        map.put("createdAt", t.getCreatedAt());
        return map;
    }
}
