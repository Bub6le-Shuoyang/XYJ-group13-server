package com.xyj.xyjserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeliverDTO {
    @JsonProperty("deliver_image")
    @NotBlank(message = "送达凭证图片不能为空")
    private String deliverImage;

    private String remark;
}