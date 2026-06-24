package com.xiaochengblog.service;

import com.xiaochengblog.mapper.ChatMessageMapper;
import com.xiaochengblog.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatHistoryService {

    private final ChatMessageMapper mapper;

    public List<Map<String, Object>> getHistory(Long userId) {
        List<ChatMessage> messages = mapper.selectByUserId(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (ChatMessage m : messages) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", m.getId());
            map.put("role", m.getRole());
            map.put("content", m.getContent());
            map.put("timestamp", m.getCreatedAt());
            result.add(map);
        }
        return result;
    }

    @Transactional
    public Map<String, Object> saveMessage(Long userId, String role, String content) {
        ChatMessage msg = ChatMessage.builder()
                .userId(userId)
                .role(role)
                .content(content)
                .build();
        mapper.insert(msg);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", msg.getId());
        result.put("role", msg.getRole());
        result.put("content", msg.getContent());
        result.put("timestamp", msg.getCreatedAt());
        return result;
    }

    @Transactional
    public void clearHistory(Long userId) {
        mapper.deleteByUserId(userId);
    }
}
