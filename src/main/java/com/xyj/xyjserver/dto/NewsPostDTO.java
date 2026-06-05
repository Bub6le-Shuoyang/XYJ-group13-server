package com.xyj.xyjserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "发布乡镇资讯请求")
public class NewsPostDTO {
    @Schema(description = "资讯标题", example = "驿站新货到啦", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "标题不能为空")
    private String title;
    
    @Schema(description = "资讯正文", example = "今日驿站到达一批新包裹...", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "内容不能为空")
    private String content;
    
    @Schema(description = "标签", example = "驿站动态")
    private String tag;
    
    @Schema(description = "是否为紧急通知", example = "false")
    @JsonProperty("is_urgent")
    private Boolean isUrgent;
}
