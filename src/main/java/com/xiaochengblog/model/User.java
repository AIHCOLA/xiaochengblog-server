package com.xiaochengblog.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("users")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;
    private String email;
    private String password;
    private String avatar;
    private String bio;
    private String link;
    private String socialLinks;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    private String role;
    private String oauth2Provider;
    private String oauth2ProviderId;
}
