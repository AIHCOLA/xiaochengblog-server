package com.xiaochengblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaochengblog.model.FlashcardKnown;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FlashcardKnownMapper extends BaseMapper<FlashcardKnown> {

    @Select("SELECT word_id FROM flashcard_known WHERE user_id = #{userId}")
    List<Integer> selectKnownWordIds(@Param("userId") Long userId);

    @Select("SELECT * FROM flashcard_known WHERE user_id = #{userId} AND word_id = #{wordId}")
    FlashcardKnown selectByUserAndWord(@Param("userId") Long userId, @Param("wordId") Integer wordId);
}
