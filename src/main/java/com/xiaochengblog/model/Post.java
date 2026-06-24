package com.xiaochengblog.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("posts")
public class Post {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    private String slug;
    private String excerpt;
    private String content;

    @TableField("cover_image")
    private String coverImage;

    /** DB column — foreign key to categories */
    @TableField("category_id")
    private Long categoryId;

    /** DB column — foreign key to users */
    @TableField("author_id")
    private Long authorId;

    // ── Non-DB fields (populated by service layer) ──

    @TableField(exist = false)
    private Category category;

    @TableField(exist = false)
    private User author;

    @TableField(exist = false)
    private Set<Tag> tags;

    // ── Scalar fields ──

    @Builder.Default
    private boolean featured = false;

    @Builder.Default
    private int likes = 0;

    @Builder.Default
    private int views = 0;

    private int readingTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
