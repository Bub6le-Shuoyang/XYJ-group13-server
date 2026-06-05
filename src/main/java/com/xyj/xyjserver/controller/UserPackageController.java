package com.xyj.xyjserver.controller;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.common.api.Result;
import com.xyj.xyjserver.common.api.ResultCode;
import com.xyj.xyjserver.common.exception.BusinessException;
import com.xyj.xyjserver.common.interceptor.AuthInterceptor;
import com.xyj.xyjserver.dto.PackageComplainDTO;
import com.xyj.xyjserver.dto.PackageRateDTO;
import com.xyj.xyjserver.dto.UserPackageCreateDTO;
import com.xyj.xyjserver.service.UserPackageService;
import com.xyj.xyjserver.vo.PackageVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/packages")
@Tag(name = "UserPackage 接口")
public class UserPackageController {

    @Autowired
    private UserPackageService userPackageService;

    /**
     * 获取我的收件列表
     */
    @Operation(summary = "获取我的收件列表")
    @GetMapping
    public Result<PageResult<PackageVO>> getMyReceivePackages(
            HttpServletRequest request,
            @Parameter(description = "包裹类型", example = "RECEIVE") @RequestParam(defaultValue = "RECEIVE") String type,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Long page,
            @Parameter(description = "每页条数", example = "10") @RequestParam(defaultValue = "10") Long size) {
        Long userId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        requireRole(request, "USER");
        return Result.success(userPackageService.getMyReceivePackages(userId, type, page, size));
    }

    /**
     * 用户提交包裹信息
     */
    @Operation(summary = "用户提交包裹信息")
    @PostMapping
    public Result<PackageVO> createPackage(
            HttpServletRequest request,
            @Validated @RequestBody UserPackageCreateDTO createDTO) {
        Long userId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        requireRole(request, "USER");
        return Result.success(userPackageService.createPackage(userId, createDTO));
    }

    /**
     * 获取包裹详情
     */
    @Operation(summary = "获取包裹详情")
    @GetMapping("/{package_id}")
    public Result<PackageVO> getPackageDetail(
            HttpServletRequest request,
            @Parameter(description = "包裹ID", example = "PKG001") @PathVariable("package_id") String packageId) {
        Long userId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        requireRole(request, "USER");
        return Result.success(userPackageService.getPackageDetail(userId, packageId));
    }

    /**
     * 用户确认签收
     */
    @Operation(summary = "用户确认签收")
    @PostMapping("/{package_id}/confirm")
    public Result<Boolean> confirmReceipt(
            HttpServletRequest request,
            @Parameter(description = "包裹ID", example = "PKG001") @PathVariable("package_id") String packageId) {
        Long userId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        requireRole(request, "USER");
        return Result.success(userPackageService.confirmReceipt(userId, packageId));
    }

    /**
     * 评价包裹服务
     */
    @Operation(summary = "评价包裹服务")
    @PostMapping("/{package_id}/rate")
    public Result<Boolean> ratePackage(
            HttpServletRequest request,
            @Parameter(description = "包裹ID", example = "PKG001") @PathVariable("package_id") String packageId,
            @Validated @RequestBody PackageRateDTO rateDTO) {
        Long userId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        requireRole(request, "USER");
        return Result.success(userPackageService.ratePackage(userId, packageId, rateDTO));
    }

    /**
     * 提交投诉
     */
    @Operation(summary = "提交投诉")
    @PostMapping("/{package_id}/complain")
    public Result<Boolean> complainPackage(
            HttpServletRequest request,
            @Parameter(description = "包裹ID", example = "PKG001") @PathVariable("package_id") String packageId,
            @Validated @RequestBody PackageComplainDTO complainDTO) {
        Long userId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        requireRole(request, "USER");
        return Result.success(userPackageService.complainPackage(userId, packageId, complainDTO));
    }

    private void requireRole(HttpServletRequest request, String expectedRole) {
        String role = (String) request.getAttribute(AuthInterceptor.USER_ROLE_ATTR);
        if (!expectedRole.equals(role)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "当前角色无权访问该接口");
        }
    }
}
