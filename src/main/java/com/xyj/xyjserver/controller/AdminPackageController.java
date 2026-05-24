package com.xyj.xyjserver.controller;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.common.api.Result;
import com.xyj.xyjserver.common.interceptor.AuthInterceptor;
import com.xyj.xyjserver.dto.PackageInboundDTO;
import com.xyj.xyjserver.dto.TaskPublishDTO;
import com.xyj.xyjserver.service.AdminPackageService;
import com.xyj.xyjserver.vo.PackageVO;
import com.xyj.xyjserver.vo.StationStatisticsVO;
import com.xyj.xyjserver.vo.TaskVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "AdminPackage 接口")
public class AdminPackageController {

    @Autowired
    private AdminPackageService adminPackageService;

    /**
     * 获取站点包裹列表
     */
    @Operation(summary = "获取站点包裹列表")
    @GetMapping("/packages")
    public Result<PageResult<PackageVO>> getStationPackages(
            HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "IN_STOCK") String status,
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "100") Long size) {
        Long adminId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        return Result.success(adminPackageService.getStationPackages(adminId, status, page, size));
    }

    /**
     * 包裹入库
     */
    @Operation(summary = "包裹入库")
    @PostMapping("/packages/{package_id}/inbound")
    public Result<Boolean> inboundPackage(
            HttpServletRequest request,
            @PathVariable("package_id") String packageId,
            @Validated @RequestBody PackageInboundDTO inboundDTO) {
        Long adminId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        return Result.success(adminPackageService.inboundPackage(adminId, packageId, inboundDTO));
    }

    /**
     * 包裹出库
     */
    @Operation(summary = "包裹出库")
    @PostMapping("/packages/{package_id}/outbound")
    public Result<Boolean> outboundPackage(
            HttpServletRequest request,
            @PathVariable("package_id") String packageId) {
        Long adminId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        return Result.success(adminPackageService.outboundPackage(adminId, packageId));
    }

    /**
     * 发布配送任务
     */
    @Operation(summary = "发布配送任务")
    @PostMapping("/tasks")
    public Result<TaskVO> publishTask(
            HttpServletRequest request,
            @Validated @RequestBody TaskPublishDTO publishDTO) {
        Long adminId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        return Result.success(adminPackageService.publishTask(adminId, publishDTO));
    }

    /**
     * 获取站点统计
     */
    @Operation(summary = "获取站点统计")
    @GetMapping("/station/statistics")
    public Result<StationStatisticsVO> getStationStatistics(HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        return Result.success(adminPackageService.getStationStatistics(adminId));
    }
}