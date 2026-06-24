package com.xiaochengblog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class PostRequest {
    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "链接不能为空")
    private String slug;

    private String excerpt;

    @NotBlank(message = "内容不能为空")
    private String content;

    private String coverImage;

    @NotBlank(message = "分类不能为空")
    private String categorySlug;

    private List<String> tagSlugs;

    private boolean featured;
}
