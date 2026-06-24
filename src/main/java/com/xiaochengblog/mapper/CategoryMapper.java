package com.xiaochengblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaochengblog.model.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
