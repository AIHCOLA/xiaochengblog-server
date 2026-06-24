package com.xiaochengblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaochengblog.model.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    @Select("SELECT * FROM comments WHERE post_id = #{postId} ORDER BY created_at ASC")
    List<Comment> selectByPostId(@Param("postId") Long postId);

    @Select("SELECT * FROM comments WHERE parent_id = #{parentId}")
    List<Comment> selectByParentId(@Param("parentId") Long parentId);
}
