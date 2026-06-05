package com.xyj.xyjserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "包裹入库请求")
public class PackageInboundDTO {
    @Schema(description = "存放货架编号", example = "A-01-03", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("shelf_number")
    @NotBlank(message = "货架号不能为空")
    private String shelfNumber;
}
