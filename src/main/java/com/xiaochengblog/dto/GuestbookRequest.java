package com.xiaochengblog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GuestbookRequest {
    @NotBlank(message = "昵称不能为空")
    private String author;

    @NotBlank(message = "内容不能为空")
    private String content;
}
