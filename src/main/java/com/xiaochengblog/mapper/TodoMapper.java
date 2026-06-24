package com.xiaochengblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaochengblog.model.Todo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TodoMapper extends BaseMapper<Todo> {

    @Select("SELECT * FROM todos WHERE user_id = #{userId} ORDER BY sort_order, created_at DESC")
    List<Todo> selectByUserId(@Param("userId") Long userId);
}
