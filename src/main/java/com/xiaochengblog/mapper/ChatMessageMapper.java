package com.xiaochengblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaochengblog.model.ChatMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {

    @Select("SELECT * FROM chat_messages WHERE user_id = #{userId} ORDER BY created_at ASC")
    List<ChatMessage> selectByUserId(@Param("userId") Long userId);

    @Delete("DELETE FROM chat_messages WHERE user_id = #{userId}")
    void deleteByUserId(@Param("userId") Long userId);
}
