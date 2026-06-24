package com.xiaochengblog.service;

import com.xiaochengblog.exception.BusinessException;
import com.xiaochengblog.mapper.FavoriteMapper;
import com.xiaochengblog.mapper.PostMapper;
import com.xiaochengblog.model.Favorite;
import com.xiaochengblog.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final PostMapper postMapper;

    public List<Map<String, Object>> getFavorites(Long userId) {
        List<Favorite> favorites = favoriteMapper.selectByUserId(userId);
        List<Long> postIds = favorites.stream().map(Favorite::getPostId).collect(Collectors.toList());

        if (postIds.isEmpty()) return Collections.emptyList();

        List<Post> posts = postMapper.selectBatchIds(postIds);
        return posts.stream().map(post -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", post.getId());
            map.put("title", post.getTitle());
            map.put("slug", post.getSlug());
            map.put("excerpt", post.getExcerpt());
            map.put("coverImage", post.getCoverImage());
            map.put("createdAt", post.getCreatedAt());
            // Category is not populated in batch queries — we populate it simply
            if (post.getCategoryId() != null) {
                // For favorites list we skip the full category population — just leave null
            }
            return map;
        }).collect(Collectors.toList());
    }

    @Transactional
    public Map<String, Object> addFavorite(Long userId, Long postId) {
        if (favoriteMapper.existsByUserAndPost(userId, postId)) {
            throw new BusinessException(400, "已经收藏过了");
        }

        if (postMapper.selectById(postId) == null) {
            throw new BusinessException(404, "文章不存在");
        }

        Favorite favorite = Favorite.builder()
                .userId(userId)
                .postId(postId)
                .build();

        favoriteMapper.insert(favorite);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("postId", postId);
        result.put("favorited", true);
        return result;
    }

    @Transactional
    public Map<String, Object> removeFavorite(Long userId, Long postId) {
        Favorite favorite = favoriteMapper.selectByUserAndPost(userId, postId);
        if (favorite == null) {
            throw new BusinessException(404, "未收藏该文章");
        }

        favoriteMapper.deleteById(favorite.getId());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("postId", postId);
        result.put("favorited", false);
        return result;
    }

    public List<Long> getFavoritePostIds(Long userId) {
        return favoriteMapper.selectByUserId(userId).stream()
                .map(Favorite::getPostId)
                .collect(Collectors.toList());
    }
}
