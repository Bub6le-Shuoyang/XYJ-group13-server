package com.xyj.xyjserver.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentDTO {
    @NotBlank(message = "评论内容不能为空")
    private String content;
}