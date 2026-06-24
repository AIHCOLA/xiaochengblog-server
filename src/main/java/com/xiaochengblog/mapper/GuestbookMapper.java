package com.xiaochengblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaochengblog.model.GuestbookEntry;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GuestbookMapper extends BaseMapper<GuestbookEntry> {

    @Select("SELECT * FROM guestbook_entries ORDER BY created_at DESC")
    List<GuestbookEntry> selectAllOrdered();
}
