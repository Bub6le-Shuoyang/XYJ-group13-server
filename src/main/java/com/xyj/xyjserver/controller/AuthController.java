package com.xyj.xyjserver.controller;

import com.xyj.xyjserver.common.api.Result;
import com.xyj.xyjserver.common.interceptor.AuthInterceptor;
import com.xyj.xyjserver.dto.LoginDTO;
import com.xyj.xyjserver.dto.RefreshTokenDTO;
import com.xyj.xyjserver.service.AuthService;
import com.xyj.xyjserver.vo.LoginResponseVO;
import com.xyj.xyjserver.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth 接口")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 账号密码登录
     */
    @Operation(summary = "账号密码登录")
    @PostMapping("/login")
    public Result<LoginResponseVO> login(@Validated @RequestBody LoginDTO loginDTO) {
        LoginResponseVO responseVO = authService.login(loginDTO);
        return Result.success(responseVO);
    }

    /**
     * 获取当前登录账号信息
     */
    @Operation(summary = "获取当前登录账号信息")
    @GetMapping("/me")
    public Result<UserVO> getCurrentUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        String role = (String) request.getAttribute(AuthInterceptor.USER_ROLE_ATTR);
        UserVO userVO = authService.getCurrentUser(userId, role);
        return Result.success(userVO);
    }

    /**
     * 刷新 Token
     */
    @Operation(summary = "刷新 Token")
    @PostMapping("/refresh")
    public Result<LoginResponseVO> refresh(@Validated @RequestBody RefreshTokenDTO refreshTokenDTO) {
        LoginResponseVO responseVO = authService.refreshToken(refreshTokenDTO);
        return Result.success(responseVO);
    }

    /**
     * 退出登录
     */
    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Boolean> logout(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        Boolean result = authService.logout(userId);
        return Result.success(result);
    }
}