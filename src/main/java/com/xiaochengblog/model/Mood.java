package com.xiaochengblog.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("moods")
public class Mood {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private LocalDate date;
    private String emoji;
    private Boolean checkedIn;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
