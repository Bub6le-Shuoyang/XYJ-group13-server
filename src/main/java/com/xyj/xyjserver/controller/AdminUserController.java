package com.xyj.xyjserver.controller;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.common.api.Result;
import com.xyj.xyjserver.dto.CreateUserDTO;
import com.xyj.xyjserver.entity.User;
import com.xyj.xyjserver.service.AdminUserManageService;
import com.xyj.xyjserver.vo.UserListVO;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@Tag(name = "AdminUser 接口")
public class AdminUserController {

    @Autowired
    private AdminUserManageService adminUserManageService;

    /**
     * 用户列表（分页+搜索）
     */
    @Operation(summary = "用户列表（分页+搜索）")
    @GetMapping
    public Result<PageResult<UserListVO>> getUserList(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Long page,
            @Parameter(description = "每页条数", example = "10") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "搜索关键词", example = "张三") @RequestParam(required = false) String keyword) {
        return Result.success(adminUserManageService.getUserList(page, size, keyword));
    }

    /**
     * 创建用户
     */
    @Operation(summary = "创建用户")
    @PostMapping
    public Result<User> createUser(@Validated @RequestBody CreateUserDTO createDTO) {
        return Result.success(adminUserManageService.createUser(createDTO));
    }

    /**
     * 更新用户
     */
    @Operation(summary = "更新用户")
    @PutMapping("/{id}")
    public Result<User> updateUser(
            @Parameter(description = "用户ID", example = "1") @PathVariable("id") Long id,
            @RequestBody User user) {
        return Result.success(adminUserManageService.updateUser(id, user));
    }

    /**
     * 删除用户
     */
    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@Parameter(description = "用户ID", example = "1") @PathVariable("id") Long id) {
        adminUserManageService.deleteUser(id);
        return Result.success();
    }
}