package com.xyj.xyjserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "刷新Token请求")
public class RefreshTokenDTO {
    @Schema(description = "刷新令牌", example = "eyJhbGciOiJIUzI1NiJ9...", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("refresh_token")
    @NotBlank(message = "refresh_token不能为空")
    private String refreshToken;
}
