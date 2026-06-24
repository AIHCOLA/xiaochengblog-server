package com.xiaochengblog.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("music_playlist")
public class MusicPlaylist {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String songId;
    private String title;
    private String artist;
    private Integer fee;
    private String platform;
    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
