package com.xyj.xyjserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "核验取件码请求")
public class VerifyPickupCodeDTO {
    @Schema(description = "取件码", example = "882356", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("pickup_code")
    @NotBlank(message = "取件码不能为空")
    private String pickupCode;
}
