package com.xiaochengblog.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("flashcard_known")
public class FlashcardKnown {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Integer wordId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
