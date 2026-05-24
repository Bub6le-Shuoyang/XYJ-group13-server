package com.xyj.xyjserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PackageInboundDTO {
    @JsonProperty("shelf_number")
    @NotBlank(message = "货架号不能为空")
    private String shelfNumber;
}