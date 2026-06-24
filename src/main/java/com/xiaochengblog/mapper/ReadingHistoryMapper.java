package com.xiaochengblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaochengblog.model.ReadingHistory;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ReadingHistoryMapper extends BaseMapper<ReadingHistory> {

    @Select("SELECT * FROM reading_history WHERE user_id = #{userId} ORDER BY read_at DESC")
    List<ReadingHistory> selectByUserId(@Param("userId") Long userId);

    @Delete("DELETE FROM reading_history WHERE user_id = #{userId}")
    void deleteByUserId(@Param("userId") Long userId);
}
