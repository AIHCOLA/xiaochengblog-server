package com.xiaochengblog.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("quick_links")
public class QuickLink {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String name;
    private String url;
    private String icon;
    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
