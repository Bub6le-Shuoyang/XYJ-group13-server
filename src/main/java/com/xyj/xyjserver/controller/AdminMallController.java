package com.xyj.xyjserver.controller;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.common.api.Result;
import com.xyj.xyjserver.common.api.ResultCode;
import com.xyj.xyjserver.common.exception.BusinessException;
import com.xyj.xyjserver.common.interceptor.AuthInterceptor;
import com.xyj.xyjserver.entity.MallItem;
import com.xyj.xyjserver.service.AdminMallService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/mall")
@Tag(name = "AdminMall 积分商城管理接口")
public class AdminMallController {

    @Autowired
    private AdminMallService adminMallService;

    @Operation(summary = "获取商品列表（支持关键词搜索）")
    @GetMapping("/items")
    public Result<PageResult<MallItem>> getItems(
            HttpServletRequest request,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        requireRole(request);
        return Result.success(adminMallService.getItems(page, size, keyword));
    }

    @Operation(summary = "创建商品")
    @PostMapping("/items")
    public Result<MallItem> createItem(
            HttpServletRequest request,
            @RequestBody MallItem item) {
        requireRole(request);
        return Result.success(adminMallService.createItem(item));
    }

    @Operation(summary = "更新商品")
    @PutMapping("/items/{id}")
    public Result<MallItem> updateItem(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody MallItem item) {
        requireRole(request);
        return Result.success(adminMallService.updateItem(id, item));
    }

    @Operation(summary = "删除商品")
    @DeleteMapping("/items/{id}")
    public Result<Void> deleteItem(
            HttpServletRequest request,
            @PathVariable Long id) {
        requireRole(request);
        adminMallService.deleteItem(id);
        return Result.success();
    }

    @Operation(summary = "调整商品库存")
    @PutMapping("/items/{id}/stock")
    public Result<Void> adjustStock(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody Map<String, Integer> body) {
        requireRole(request);
        Integer delta = body.get("delta");
        if (delta == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "缺少 delta 参数");
        }
        adminMallService.adjustStock(id, delta);
        return Result.success();
    }

    private void requireRole(HttpServletRequest request) {
        String role = (String) request.getAttribute(AuthInterceptor.USER_ROLE_ATTR);
        if (!"ADMIN".equals(role)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "当前角色无权访问管理员接口");
        }
    }
}
