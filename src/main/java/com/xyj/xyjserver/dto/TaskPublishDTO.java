package com.xyj.xyjserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TaskPublishDTO {
    @JsonProperty("package_id")
    @NotBlank(message = "包裹ID不能为空")
    private String packageId;

    @JsonProperty("reward_amount")
    @NotNull(message = "任务赏金不能为空")
    private BigDecimal rewardAmount;
}