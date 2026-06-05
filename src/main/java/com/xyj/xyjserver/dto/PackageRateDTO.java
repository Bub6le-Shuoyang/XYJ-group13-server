package com.xyj.xyjserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "评价包裹请求")
public class PackageRateDTO {
    @Schema(description = "评分（1-5）", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最小为1")
    @Max(value = 5, message = "评分最大为5")
    private Integer score;

    @Schema(description = "评价内容", example = "配送很快，服务很好！", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "评价内容不能为空")
    private String comment;
}
