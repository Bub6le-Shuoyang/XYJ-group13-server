package com.xyj.xyjserver.controller;

import com.xyj.xyjserver.common.api.Result;
import com.xyj.xyjserver.dto.AdminLoginDTO;
import com.xyj.xyjserver.dto.AdminRegisterDTO;
import com.xyj.xyjserver.dto.AdminResetPasswordDTO;
import com.xyj.xyjserver.dto.SendEmailCodeDTO;
import com.xyj.xyjserver.service.AdminAuthService;
import com.xyj.xyjserver.vo.CaptchaResponseVO;
import com.xyj.xyjserver.vo.LoginResponseVO;
import com.xyj.xyjserver.vo.SendCaptchaResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/auth")
@Tag(name = "AdminAuth 接口")
public class AdminAuthController {

    @Autowired
    private AdminAuthService adminAuthService;

    /**
     * 获取图形验证码（后台预留，移动端不使用）
     */
    @Operation(summary = "获取图形验证码（后台预留，移动端不使用）")
    @GetMapping("/captcha")
    public Result<CaptchaResponseVO> getCaptcha() {
        return Result.success(adminAuthService.getCaptcha());
    }

    /**
     * 发送邮箱验证码（后台预留，移动端不使用）
     */
    @Operation(summary = "发送邮箱验证码（后台预留，移动端不使用）")
    @PostMapping("/send-email-code")
    public Result<SendCaptchaResponseVO> sendEmailCode(@Validated @RequestBody SendEmailCodeDTO sendEmailCodeDTO) {
        return Result.success(adminAuthService.sendEmailCode(sendEmailCodeDTO));
    }

    /**
     * 管理员注册（后台预留，移动端不使用）
     */
    @Operation(summary = "管理员注册（后台预留，移动端不使用）")
    @PostMapping("/register")
    public Result<LoginResponseVO> register(@Validated @RequestBody AdminRegisterDTO adminRegisterDTO) {
        return Result.success(adminAuthService.register(adminRegisterDTO));
    }

    /**
     * 管理员账号密码登录（兼容后台）
     */
    @Operation(summary = "管理员账号密码登录（兼容后台）")
    @PostMapping("/login")
    public Result<LoginResponseVO> login(@Validated @RequestBody AdminLoginDTO loginDTO) {
        LoginResponseVO responseVO = adminAuthService.login(loginDTO);
        return Result.success(responseVO);
    }

    /**
     * 重置密码（后台预留，移动端不使用）
     */
    @Operation(summary = "重置密码（后台预留，移动端不使用）")
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@Validated @RequestBody AdminResetPasswordDTO resetPasswordDTO) {
        adminAuthService.resetPassword(resetPasswordDTO);
        return Result.success();
    }
}