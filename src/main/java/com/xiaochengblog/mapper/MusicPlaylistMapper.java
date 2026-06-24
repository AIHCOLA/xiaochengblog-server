package com.xiaochengblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaochengblog.model.MusicPlaylist;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MusicPlaylistMapper extends BaseMapper<MusicPlaylist> {

    @Select("SELECT * FROM music_playlist WHERE user_id = #{userId} ORDER BY sort_order")
    List<MusicPlaylist> selectByUserId(Long userId);

    @Delete("DELETE FROM music_playlist WHERE user_id = #{userId}")
    int deleteByUserId(Long userId);
}
