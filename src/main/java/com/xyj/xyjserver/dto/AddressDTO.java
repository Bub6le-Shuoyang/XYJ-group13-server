package com.xyj.xyjserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddressDTO {
    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    @NotBlank(message = "地址不能为空")
    private String address;

    @JsonProperty("is_default")
    @NotNull(message = "是否默认地址不能为空")
    private Boolean isDefault;
}