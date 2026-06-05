package com.xyj.xyjserver.controller;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.common.api.Result;
import com.xyj.xyjserver.common.api.ResultCode;
import com.xyj.xyjserver.common.exception.BusinessException;
import com.xyj.xyjserver.common.interceptor.AuthInterceptor;
import com.xyj.xyjserver.service.AdminUserFinanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/user-finance")
@Tag(name = "AdminUserFinance 用户财务管理接口")
public class AdminUserFinanceController {

    @Autowired
    private AdminUserFinanceService adminUserFinanceService;

    @Operation(summary = "获取用户积分账户列表")
    @GetMapping("/accounts")
    public Result<PageResult<Map<String, Object>>> getAccounts(
            HttpServletRequest request,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long size) {
        requireRole(request);
        return Result.success(adminUserFinanceService.getAccounts(page, size));
    }

    @Operation(summary = "调整用户积分（正数增加，负数扣减）")
    @PostMapping("/points/adjust")
    public Result<Void> adjustPoints(
            HttpServletRequest request,
            @RequestBody Map<String, Object> body) {
        requireRole(request);
        Long userId = toLong(body.get("userId"));
        Integer points = toInt(body.get("points"));
        if (userId == null || points == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "缺少 userId 或 points 参数");
        }
        adminUserFinanceService.adjustPoints(userId, points);
        return Result.success();
    }

    @Operation(summary = "调整用户余额（正数增加，负数扣减）")
    @PostMapping("/balance/adjust")
    public Result<Void> adjustBalance(
            HttpServletRequest request,
            @RequestBody Map<String, Object> body) {
        requireRole(request);
        Long userId = toLong(body.get("userId"));
        BigDecimal amount = toBigDecimal(body.get("amount"));
        if (userId == null || amount == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "缺少 userId 或 amount 参数");
        }
        adminUserFinanceService.adjustBalance(userId, amount);
        return Result.success();
    }

    @Operation(summary = "发放优惠券")
    @PostMapping("/coupons/issue")
    public Result<Void> issueCoupon(
            HttpServletRequest request,
            @RequestBody Map<String, Object> body) {
        requireRole(request);
        Long userId = toLong(body.get("userId"));
        String name = (String) body.get("name");
        BigDecimal amount = toBigDecimal(body.get("amount"));
        String source = (String) body.get("source");
        if (userId == null || name == null || amount == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "缺少必要参数");
        }
        adminUserFinanceService.issueCoupon(userId, name, amount, source != null ? source : "ADMIN_ISSUE");
        return Result.success();
    }

    private void requireRole(HttpServletRequest request) {
        String role = (String) request.getAttribute(AuthInterceptor.USER_ROLE_ATTR);
        if (!"ADMIN".equals(role)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "当前角色无权访问管理员接口");
        }
    }

    private Long toLong(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Number) return ((Number) obj).longValue();
        try { return Long.parseLong(obj.toString()); } catch (NumberFormatException e) { return null; }
    }

    private Integer toInt(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Number) return ((Number) obj).intValue();
        try { return Integer.parseInt(obj.toString()); } catch (NumberFormatException e) { return null; }
    }

    private BigDecimal toBigDecimal(Object obj) {
        if (obj == null) return null;
        if (obj instanceof BigDecimal) return (BigDecimal) obj;
        try { return new BigDecimal(obj.toString()); } catch (NumberFormatException e) { return null; }
    }
}
