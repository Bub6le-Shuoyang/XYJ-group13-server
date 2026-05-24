package com.xyj.xyjserver.controller;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.common.api.Result;
import com.xyj.xyjserver.common.interceptor.AuthInterceptor;
import com.xyj.xyjserver.dto.PackageComplainDTO;
import com.xyj.xyjserver.dto.PackageRateDTO;
import com.xyj.xyjserver.service.UserPackageService;
import com.xyj.xyjserver.vo.PackageVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.Operation;
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
            @RequestParam(defaultValue = "RECEIVE") String type,
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size) {
        Long userId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        return Result.success(userPackageService.getMyReceivePackages(userId, type, page, size));
    }

    /**
     * 获取包裹详情
     */
    @Operation(summary = "获取包裹详情")
    @GetMapping("/{package_id}")
    public Result<PackageVO> getPackageDetail(
            HttpServletRequest request,
            @PathVariable("package_id") String packageId) {
        Long userId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        return Result.success(userPackageService.getPackageDetail(userId, packageId));
    }

    /**
     * 用户确认签收
     */
    @Operation(summary = "用户确认签收")
    @PostMapping("/{package_id}/confirm")
    public Result<Boolean> confirmReceipt(
            HttpServletRequest request,
            @PathVariable("package_id") String packageId) {
        Long userId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        return Result.success(userPackageService.confirmReceipt(userId, packageId));
    }

    /**
     * 评价包裹服务
     */
    @Operation(summary = "评价包裹服务")
    @PostMapping("/{package_id}/rate")
    public Result<Boolean> ratePackage(
            HttpServletRequest request,
            @PathVariable("package_id") String packageId,
            @Validated @RequestBody PackageRateDTO rateDTO) {
        Long userId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        return Result.success(userPackageService.ratePackage(userId, packageId, rateDTO));
    }

    /**
     * 提交投诉
     */
    @Operation(summary = "提交投诉")
    @PostMapping("/{package_id}/complain")
    public Result<Boolean> complainPackage(
            HttpServletRequest request,
            @PathVariable("package_id") String packageId,
            @Validated @RequestBody PackageComplainDTO complainDTO) {
        Long userId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        return Result.success(userPackageService.complainPackage(userId, packageId, complainDTO));
    }
}