package com.xiaochengblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaochengblog.model.Favorite;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {

    @Select("SELECT * FROM favorites WHERE user_id = #{userId}")
    List<Favorite> selectByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM favorites WHERE user_id = #{userId} AND post_id = #{postId}")
    Favorite selectByUserAndPost(@Param("userId") Long userId, @Param("postId") Long postId);

    @Select("SELECT EXISTS(SELECT 1 FROM favorites WHERE user_id = #{userId} AND post_id = #{postId})")
    boolean existsByUserAndPost(@Param("userId") Long userId, @Param("postId") Long postId);
}
