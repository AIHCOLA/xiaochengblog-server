package com.xiaochengblog.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("reading_history")
public class ReadingHistory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Long postId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime readAt;
}
