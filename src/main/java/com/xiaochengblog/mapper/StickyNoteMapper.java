package com.xiaochengblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaochengblog.model.StickyNote;
import org.apache.ibatis.annotations.*;

@Mapper
public interface StickyNoteMapper extends BaseMapper<StickyNote> {

    @Select("SELECT * FROM sticky_notes WHERE user_id = #{userId}")
    StickyNote selectByUserId(@Param("userId") Long userId);
}
