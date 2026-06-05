package com.xyj.xyjserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "统一登录请求")
public class LoginDTO {
    @Schema(description = "登录账号（邮箱/手机号/用户名）", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "账号不能为空")
    private String account;

    @Schema(description = "登录密码", example = "MyPass123!", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "角色：ADMIN / USER / COURIER", example = "USER", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "角色不能为空")
    private String role;
}
