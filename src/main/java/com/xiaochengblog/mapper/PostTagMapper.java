package com.xiaochengblog.mapper;

import com.xiaochengblog.model.Tag;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PostTagMapper {

    @Insert("INSERT INTO post_tags (post_id, tag_id) VALUES (#{postId}, #{tagId})")
    void insert(@Param("postId") Long postId, @Param("tagId") Long tagId);

    @Delete("DELETE FROM post_tags WHERE post_id = #{postId}")
    void deleteByPostId(@Param("postId") Long postId);

    @Select("SELECT t.* FROM tags t INNER JOIN post_tags pt ON t.id = pt.tag_id WHERE pt.post_id = #{postId}")
    List<Tag> selectTagsByPostId(@Param("postId") Long postId);
}
