package com.xyj.xyjserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyPickupCodeDTO {
    @JsonProperty("pickup_code")
    @NotBlank(message = "取件码不能为空")
    private String pickupCode;
}