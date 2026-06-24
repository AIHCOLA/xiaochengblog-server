package com.xiaochengblog.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("tags")
public class Tag {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String slug;
}
