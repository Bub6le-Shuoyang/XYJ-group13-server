package com.xyj.xyjserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "发送邮箱验证码请求")
public class SendEmailCodeDTO {
    @Schema(description = "目标邮箱", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "图形验证码ID", example = "captcha-uuid-xxx", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "图形验证码ID不能为空")
    private String captchaId;

    @Schema(description = "图形验证码答案", example = "a3bK", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "图形验证码不能为空")
    private String captchaCode;
}
