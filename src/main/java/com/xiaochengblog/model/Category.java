package com.xiaochengblog.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("categories")
public class Category {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String slug;
    private String description;

    @Builder.Default
    private String color = "#6c5ce7";
}
