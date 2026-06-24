package com.xiaochengblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaochengblog.model.Countdown;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CountdownMapper extends BaseMapper<Countdown> {

    @Select("SELECT * FROM countdowns WHERE user_id = #{userId} ORDER BY target_date ASC")
    List<Countdown> selectByUserId(@Param("userId") Long userId);
}
