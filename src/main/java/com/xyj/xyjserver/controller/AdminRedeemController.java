package com.xyj.xyjserver.controller;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.common.api.Result;
import com.xyj.xyjserver.common.api.ResultCode;
import com.xyj.xyjserver.common.exception.BusinessException;
import com.xyj.xyjserver.common.interceptor.AuthInterceptor;
import com.xyj.xyjserver.service.AdminRedeemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/redeem")
@Tag(name = "AdminRedeem 兑换记录管理接口")
public class AdminRedeemController {

    @Autowired
    private AdminRedeemService adminRedeemService;

    @Operation(summary = "获取兑换记录列表")
    @GetMapping("")
    public Result<PageResult<Map<String, Object>>> getRecords(
            HttpServletRequest request,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long size) {
        requireRole(request);
        return Result.success(adminRedeemService.getRecords(page, size));
    }

    @Operation(summary = "标记兑换记录为已履约")
    @PutMapping("/{id}/fulfill")
    public Result<Void> fulfillRecord(
            HttpServletRequest request,
            @PathVariable Long id) {
        requireRole(request);
        adminRedeemService.fulfillRecord(id);
        return Result.success();
    }

    @Operation(summary = "取消兑换记录")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancelRecord(
            HttpServletRequest request,
            @PathVariable Long id) {
        requireRole(request);
        adminRedeemService.cancelRecord(id);
        return Result.success();
    }

    private void requireRole(HttpServletRequest request) {
        String role = (String) request.getAttribute(AuthInterceptor.USER_ROLE_ATTR);
        if (!"ADMIN".equals(role)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "当前角色无权访问管理员接口");
        }
    }
}
