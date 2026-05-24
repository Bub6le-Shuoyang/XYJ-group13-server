package com.xyj.xyjserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewsPostDTO {
    @NotBlank(message = "标题不能为空")
    private String title;
    
    @NotBlank(message = "内容不能为空")
    private String content;
    
    private String tag;
    
    @JsonProperty("is_urgent")
    private Boolean isUrgent;
}