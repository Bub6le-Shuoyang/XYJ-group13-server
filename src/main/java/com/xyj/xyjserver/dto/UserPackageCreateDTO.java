package com.xyj.xyjserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserPackageCreateDTO {
    @JsonProperty("order_no")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @JsonProperty("station_id")
    @NotNull(message = "寄件驿站不能为空")
    private Long stationId;

    @JsonProperty("receiver_name")
    @NotBlank(message = "收件人不能为空")
    private String receiverName;

    @JsonProperty("receiver_phone")
    private String receiverPhone;

    @NotBlank(message = "配送地址不能为空")
    private String address;

    private Double weight;

    @JsonProperty("reward_amount")
    private BigDecimal rewardAmount;

    private Double lat;
    private Double lng;
}
