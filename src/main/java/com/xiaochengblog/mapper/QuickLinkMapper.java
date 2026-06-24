package com.xiaochengblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaochengblog.model.QuickLink;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface QuickLinkMapper extends BaseMapper<QuickLink> {

    @Select("SELECT * FROM quick_links WHERE user_id = #{userId} ORDER BY sort_order, id")
    List<QuickLink> selectByUserId(@Param("userId") Long userId);
}
