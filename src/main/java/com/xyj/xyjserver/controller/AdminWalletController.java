package com.xyj.xyjserver.controller;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.common.api.Result;
import com.xyj.xyjserver.common.api.ResultCode;
import com.xyj.xyjserver.common.exception.BusinessException;
import com.xyj.xyjserver.common.interceptor.AuthInterceptor;
import com.xyj.xyjserver.service.AdminWalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/wallet")
@Tag(name = "AdminWallet 钱包流水管理接口")
public class AdminWalletController {

    @Autowired
    private AdminWalletService adminWalletService;

    @Operation(summary = "获取钱包流水记录")
    @GetMapping("/transactions")
    public Result<PageResult<Map<String, Object>>> getTransactions(
            HttpServletRequest request,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "用户ID筛选") @RequestParam(required = false) Long userId,
            @Parameter(description = "交易类型筛选") @RequestParam(required = false) String type) {
        requireRole(request);
        return Result.success(adminWalletService.getTransactions(page, size, userId, type));
    }

    private void requireRole(HttpServletRequest request) {
        String role = (String) request.getAttribute(AuthInterceptor.USER_ROLE_ATTR);
        if (!"ADMIN".equals(role)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "当前角色无权访问管理员接口");
        }
    }
}
