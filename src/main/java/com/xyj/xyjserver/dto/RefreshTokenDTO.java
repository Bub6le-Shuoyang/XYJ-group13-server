package com.xyj.xyjserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenDTO {
    @JsonProperty("refresh_token")
    @NotBlank(message = "refresh_token不能为空")
    private String refreshToken;
}