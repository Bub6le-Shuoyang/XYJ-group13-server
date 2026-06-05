package com.xyj.xyjserver.controller;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.common.api.Result;
import com.xyj.xyjserver.common.api.ResultCode;
import com.xyj.xyjserver.common.exception.BusinessException;
import com.xyj.xyjserver.common.interceptor.AuthInterceptor;
import com.xyj.xyjserver.entity.AdminNotification;
import com.xyj.xyjserver.service.AdminNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/notifications")
@Tag(name = "AdminNotification 通知管理接口")
public class AdminNotificationController {

    @Autowired
    private AdminNotificationService adminNotificationService;

    @Operation(summary = "获取通知列表（分页）")
    @GetMapping
    public Result<PageResult<AdminNotification>> getNotifications(
            HttpServletRequest request,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Long page,
            @Parameter(description = "每页条数", example = "10") @RequestParam(defaultValue = "10") Long size) {
        requireAdmin(request);
        return Result.success(adminNotificationService.getNotifications(page, size));
    }

    @Operation(summary = "获取未读通知数量")
    @GetMapping("/unread-count")
    public Result<Map<String, Long>> getUnreadCount(HttpServletRequest request) {
        requireAdmin(request);
        long count = adminNotificationService.getUnreadCount();
        return Result.success(Map.of("count", count));
    }

    @Operation(summary = "标记单条通知为已读")
    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(
            HttpServletRequest request,
            @Parameter(description = "通知ID") @PathVariable Long id) {
        requireAdmin(request);
        adminNotificationService.markAsRead(id);
        return Result.success();
    }

    @Operation(summary = "标记全部通知为已读")
    @PutMapping("/read-all")
    public Result<Void> markAllAsRead(HttpServletRequest request) {
        requireAdmin(request);
        adminNotificationService.markAllAsRead();
        return Result.success();
    }

    @Operation(summary = "创建通知（测试用）")
    @PostMapping
    public Result<Void> createNotification(
            HttpServletRequest request,
            @RequestBody Map<String, String> body) {
        requireAdmin(request);
        String title = body.get("title");
        String content = body.get("content");
        String type = body.get("type");
        if (title == null || title.isBlank()) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "通知标题不能为空");
        }
        adminNotificationService.notify(title, content, type);
        return Result.success();
    }

    private void requireAdmin(HttpServletRequest request) {
        String role = (String) request.getAttribute(AuthInterceptor.USER_ROLE_ATTR);
        if (!"ADMIN".equals(role)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "当前角色无权访问管理员接口");
        }
    }
}
