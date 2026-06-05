package com.xyj.xyjserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "包裹审批请求")
public class PackageApproveDTO {
    @Schema(description = "配送赏金（元）", example = "5.00")
    @JsonProperty("reward_amount")
    private BigDecimal rewardAmount;
}
