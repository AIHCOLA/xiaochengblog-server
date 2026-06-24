package com.xiaochengblog.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiaochengblog.dto.CommentRequest;
import com.xiaochengblog.exception.BusinessException;
import com.xiaochengblog.mapper.CommentMapper;
import com.xiaochengblog.model.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper commentMapper;

    public List<Map<String, Object>> getCommentsByPostId(Long postId) {
        return commentMapper.selectByPostId(postId).stream()
                .map(this::toCommentMap)
                .collect(Collectors.toList());
    }

    @Transactional
    public Map<String, Object> addComment(Long postId, CommentRequest request) {
        Comment comment = Comment.builder()
                .postId(postId)
                .parentId(request.getParentId())
                .author(request.getAuthor())
                .email(request.getEmail())
                .content(request.getContent())
                .likes(0)
                .build();

        commentMapper.insert(comment);
        return toCommentMap(comment);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) throw new BusinessException(404, "评论不存在");

        // Delete replies
        List<Comment> replies = commentMapper.selectByParentId(commentId);
        for (Comment reply : replies) {
            commentMapper.deleteById(reply.getId());
        }
        commentMapper.deleteById(commentId);
    }

    @Transactional
    public Map<String, Object> likeComment(Long commentId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) throw new BusinessException(404, "评论不存在");

        comment.setLikes(comment.getLikes() + 1);
        commentMapper.updateById(comment);
        return toCommentMap(comment);
    }

    private Map<String, Object> toCommentMap(Comment comment) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", comment.getId());
        map.put("postId", comment.getPostId());
        map.put("parentId", comment.getParentId());
        map.put("author", comment.getAuthor());
        map.put("email", comment.getEmail());
        map.put("content", comment.getContent());
        map.put("likes", comment.getLikes());
        map.put("createdAt", comment.getCreatedAt());
        return map;
    }
}
