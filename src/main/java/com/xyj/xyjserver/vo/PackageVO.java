package com.xyj.xyjserver.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
import java.math.BigDecimal;

@Data
public class PackageVO {
    @JsonProperty("package_id")
    private String packageId;

    @JsonProperty("task_id")
    private String taskId;

    private String name;

    @JsonProperty("receiver_name")
    private String receiverName;

    @JsonProperty("receiver_phone")
    private String receiverPhone;

    private String address;

    private Double weight;

    @JsonProperty("estimated_fee")
    private BigDecimal estimatedFee;

    private String status;

    @JsonProperty("pickup_code")
    private String pickupCode;

    @JsonProperty("sender_name")
    private String senderName;

    @JsonProperty("reward_amount")
    private BigDecimal rewardAmount;

    private List<String> timeline;

    @JsonProperty("courier_name")
    private String courierName;

    @JsonProperty("station_id")
    private String stationId;

    @JsonProperty("station_name")
    private String stationName;

    private Double lat;
    private Double lng;
}