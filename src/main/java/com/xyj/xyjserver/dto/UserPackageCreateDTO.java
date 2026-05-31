package com.xyj.xyjserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserPackageCreateDTO {
    @NotBlank(message = "包裹名称不能为空")
    private String name;

    @JsonProperty("sender_name")
    private String senderName;

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
