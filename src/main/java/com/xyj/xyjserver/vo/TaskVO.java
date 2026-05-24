package com.xyj.xyjserver.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TaskVO {
    @JsonProperty("task_id")
    private String taskId;
    
    @JsonProperty("package_id")
    private String packageId;
    
    @JsonProperty("package_name")
    private String packageName;
    
    @JsonProperty("pickup_address")
    private String pickupAddress;
    
    @JsonProperty("deliver_address")
    private String deliverAddress;
    
    @JsonProperty("reward_amount")
    private BigDecimal rewardAmount;
    
    private String status;
    
    @JsonProperty("pickup_code_masked")
    private String pickupCodeMasked;
    
    @JsonProperty("created_at")
    private Date createdAt;
    
    @JsonProperty("completed_at")
    private Date completedAt;
}