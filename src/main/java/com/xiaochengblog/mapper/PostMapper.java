package com.xiaochengblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaochengblog.model.Post;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PostMapper extends BaseMapper<Post> {

    @Select("SELECT * FROM posts WHERE featured = 1 ORDER BY created_at DESC")
    List<Post> selectFeatured();

    @Select("SELECT * FROM posts WHERE category_id = (SELECT id FROM categories WHERE slug = #{slug}) ORDER BY created_at DESC")
    List<Post> selectByCategorySlug(@Param("slug") String slug);

    @Select("SELECT p.* FROM posts p INNER JOIN post_tags pt ON p.id = pt.post_id INNER JOIN tags t ON pt.tag_id = t.id WHERE t.slug = #{slug} ORDER BY p.created_at DESC")
    List<Post> selectByTagSlug(@Param("slug") String slug);

    @Select("SELECT * FROM posts WHERE title LIKE CONCAT('%',#{q},'%') OR excerpt LIKE CONCAT('%',#{q},'%') OR content LIKE CONCAT('%',#{q},'%') ORDER BY created_at DESC LIMIT #{limit} OFFSET #{offset}")
    List<Post> searchPaged(@Param("q") String q, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM posts WHERE title LIKE CONCAT('%',#{q},'%') OR excerpt LIKE CONCAT('%',#{q},'%') OR content LIKE CONCAT('%',#{q},'%')")
    long countSearch(@Param("q") String q);

    @Select("SELECT * FROM posts WHERE author_id = #{authorId} ORDER BY created_at DESC")
    List<Post> selectByAuthorId(@Param("authorId") Long authorId);

    @Select("SELECT * FROM posts WHERE slug = #{slug}")
    Post selectBySlug(@Param("slug") String slug);

    @Select("SELECT EXISTS(SELECT 1 FROM posts WHERE slug = #{slug})")
    boolean existsBySlug(@Param("slug") String slug);

    // Legacy list-all (kept for internal use)
    @Select("SELECT * FROM posts ORDER BY created_at DESC")
    List<Post> selectAll();
}
