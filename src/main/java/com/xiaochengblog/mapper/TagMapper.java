package com.xiaochengblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaochengblog.model.Tag;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {
}
