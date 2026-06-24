package com.xiaochengblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaochengblog.model.HealthReminder;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface HealthReminderMapper extends BaseMapper<HealthReminder> {

    @Select("SELECT * FROM health_reminders WHERE user_id = #{userId}")
    List<HealthReminder> selectByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM health_reminders WHERE user_id = #{userId} AND reminder_id = #{reminderId}")
    HealthReminder selectByUserAndReminder(@Param("userId") Long userId, @Param("reminderId") String reminderId);
}
