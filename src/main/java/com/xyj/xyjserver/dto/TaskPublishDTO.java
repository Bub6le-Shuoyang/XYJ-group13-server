package com.xyj.xyjserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "发布配送任务请求")
public class TaskPublishDTO {
    @Schema(description = "包裹ID", example = "PKG20260601001", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("package_id")
    @NotBlank(message = "包裹ID不能为空")
    private String packageId;

    @Schema(description = "任务赏金（元）", example = "5.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("reward_amount")
    @NotNull(message = "任务赏金不能为空")
    private BigDecimal rewardAmount;
}
