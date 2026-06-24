package com.xiaochengblog.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("chat_messages")
public class ChatMessage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String role;
    private String content;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
