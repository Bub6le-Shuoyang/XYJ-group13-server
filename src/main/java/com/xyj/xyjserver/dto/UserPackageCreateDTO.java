package com.xyj.xyjserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "用户提交包裹请求")
public class UserPackageCreateDTO {
    @Schema(description = "快递单号", example = "SF1234567890", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("order_no")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @Schema(description = "寄件驿站ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("station_id")
    @NotNull(message = "寄件驿站不能为空")
    private Long stationId;

    @Schema(description = "收件人姓名", example = "张三", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("receiver_name")
    @NotBlank(message = "收件人不能为空")
    private String receiverName;

    @Schema(description = "收件人手机号", example = "13800138000")
    @JsonProperty("receiver_phone")
    private String receiverPhone;

    @Schema(description = "配送地址", example = "北京交通大学南门驿站", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "配送地址不能为空")
    private String address;

    @Schema(description = "包裹重量（kg）", example = "2.5")
    private Double weight;

    @Schema(description = "配送赏金（元）", example = "5.00")
    @JsonProperty("reward_amount")
    private BigDecimal rewardAmount;

    @Schema(description = "纬度", example = "39.9528")
    private Double lat;

    @Schema(description = "经度", example = "116.3416")
    private Double lng;
}
