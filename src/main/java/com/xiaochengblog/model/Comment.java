package com.xiaochengblog.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("comments")
public class Comment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long postId;
    private Long parentId;
    private String author;
    private String email;
    private String content;

    @Builder.Default
    private int likes = 0;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
