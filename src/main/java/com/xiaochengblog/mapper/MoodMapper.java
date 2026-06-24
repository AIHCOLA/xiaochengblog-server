package com.xiaochengblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaochengblog.model.Mood;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface MoodMapper extends BaseMapper<Mood> {

    @Select("SELECT * FROM moods WHERE user_id = #{userId} ORDER BY date")
    List<Mood> selectByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM moods WHERE user_id = #{userId} AND date BETWEEN #{startDate} AND #{endDate} ORDER BY date")
    List<Mood> selectByUserIdAndDateRange(@Param("userId") Long userId,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

    @Select("SELECT * FROM moods WHERE user_id = #{userId} AND date = #{date}")
    Mood selectByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);
}
