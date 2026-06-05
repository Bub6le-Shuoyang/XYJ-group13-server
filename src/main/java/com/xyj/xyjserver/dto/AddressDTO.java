package com.xyj.xyjserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "新增地址请求")
public class AddressDTO {
    @Schema(description = "收件人姓名", example = "张三", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "姓名不能为空")
    private String name;

    @Schema(description = "联系电话", example = "13800138000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "手机号不能为空")
    private String phone;

    @Schema(description = "详细地址", example = "北京市海淀区上园村3号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "地址不能为空")
    private String address;

    @Schema(description = "是否设为默认地址", example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("is_default")
    @NotNull(message = "是否默认地址不能为空")
    private Boolean isDefault;
}
