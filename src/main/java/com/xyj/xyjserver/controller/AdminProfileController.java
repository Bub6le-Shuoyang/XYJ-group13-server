package com.xyj.xyjserver.controller;

import com.xyj.xyjserver.common.api.Result;
import com.xyj.xyjserver.common.interceptor.AuthInterceptor;
import com.xyj.xyjserver.entity.Admin;
import com.xyj.xyjserver.service.AdminProfileService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/profile")
@Tag(name = "AdminProfile 接口")
public class AdminProfileController {

    @Autowired
    private AdminProfileService adminProfileService;

    /**
     * 获取当前管理员信息
     */
    @Operation(summary = "获取当前管理员信息")
    @GetMapping
    public Result<Admin> getCurrentAdmin(HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        return Result.success(adminProfileService.getCurrentAdmin(adminId));
    }

    /**
     * 更新当前管理员资料
     */
    @Operation(summary = "更新当前管理员资料")
    @PutMapping
    public Result<Admin> updateCurrentAdmin(HttpServletRequest request, @RequestBody Admin admin) {
        Long adminId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        return Result.success(adminProfileService.updateCurrentAdmin(adminId, admin));
    }
}