package com.xyj.xyjserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "管理员登录请求")
public class AdminLoginDTO {
    @Schema(description = "管理员账号（用户名/邮箱/手机号均可）", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "账号不能为空")
    private String email;

    @Schema(description = "登录密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码不能为空")
    private String password;
}
