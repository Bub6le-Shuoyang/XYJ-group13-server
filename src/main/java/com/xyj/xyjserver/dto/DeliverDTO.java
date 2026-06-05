package com.xyj.xyjserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "上传送达凭证请求")
public class DeliverDTO {
    @Schema(description = "送达凭证图片URL", example = "/uploads/deliver/proof_001.jpg", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("deliver_image")
    @NotBlank(message = "送达凭证图片不能为空")
    private String deliverImage;

    @Schema(description = "备注信息", example = "已放在门口")
    private String remark;
}
