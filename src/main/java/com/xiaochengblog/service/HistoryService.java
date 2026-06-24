package com.xiaochengblog.service;

import com.xiaochengblog.exception.BusinessException;
import com.xiaochengblog.mapper.PostMapper;
import com.xiaochengblog.mapper.ReadingHistoryMapper;
import com.xiaochengblog.model.Post;
import com.xiaochengblog.model.ReadingHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final ReadingHistoryMapper historyMapper;
    private final PostMapper postMapper;

    public List<Map<String, Object>> getHistory(Long userId) {
        List<ReadingHistory> histories = historyMapper.selectByUserId(userId);
        List<Long> postIds = histories.stream().map(ReadingHistory::getPostId).collect(Collectors.toList());

        if (postIds.isEmpty()) return Collections.emptyList();

        List<Post> posts = postMapper.selectBatchIds(postIds);
        Map<Long, Post> postMap = posts.stream()
                .collect(Collectors.toMap(Post::getId, p -> p));

        return histories.stream()
                .filter(h -> postMap.containsKey(h.getPostId()))
                .map(h -> {
                    Post post = postMap.get(h.getPostId());
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("postId", post.getId());
                    map.put("postTitle", post.getTitle());
                    map.put("postSlug", post.getSlug());
                    map.put("readAt", h.getReadAt());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void addToHistory(Long userId, Long postId) {
        if (postMapper.selectById(postId) == null) {
            throw new BusinessException(404, "文章不存在");
        }

        ReadingHistory history = ReadingHistory.builder()
                .userId(userId)
                .postId(postId)
                .build();

        historyMapper.insert(history);
    }

    @Transactional
    public void clearHistory(Long userId) {
        historyMapper.deleteByUserId(userId);
    }
}
