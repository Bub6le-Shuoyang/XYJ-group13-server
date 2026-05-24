package com.xyj.xyjserver.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUserDTO {
    @NotBlank(message = "账号不能为空")
    private String account;
    
    @NotBlank(message = "密码不能为空")
    private String password;
    
    private String email;
    private String phone;
    private String nickname;
    private String role;
}