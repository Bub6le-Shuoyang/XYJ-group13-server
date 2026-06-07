package com.xyj.xyjserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateAvatarDTO {
    @NotBlank(message = "头像地址不能为空")
    @JsonProperty("avatar_url")
    private String avatarUrl;
}
