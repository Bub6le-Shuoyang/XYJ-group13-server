package com.xyj.xyjserver.controller;

import com.xyj.xyjserver.common.api.Result;
import com.xyj.xyjserver.service.AdminAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/analytics")
@Tag(name = "AdminAnalytics 数据分析接口")
public class AdminAnalyticsController {

    @Autowired
    private AdminAnalyticsService adminAnalyticsService;

    @Operation(summary = "获取包裹趋势数据")
    @GetMapping("/package-trend")
    public Result<List<Map<String, Object>>> getPackageTrend(
            @Parameter(description = "天数", example = "7") @RequestParam(defaultValue = "7") int days) {
        return Result.success(adminAnalyticsService.getPackageTrend(days));
    }

    @Operation(summary = "获取用户增长数据")
    @GetMapping("/user-growth")
    public Result<List<Map<String, Object>>> getUserGrowth(
            @Parameter(description = "天数", example = "7") @RequestParam(defaultValue = "7") int days) {
        return Result.success(adminAnalyticsService.getUserGrowth(days));
    }

    @Operation(summary = "获取包裹状态分布")
    @GetMapping("/package-status")
    public Result<List<Map<String, Object>>> getPackageStatusDistribution() {
        return Result.success(adminAnalyticsService.getPackageStatusDistribution());
    }

    @Operation(summary = "获取快递员效率排行")
    @GetMapping("/courier-efficiency")
    public Result<List<Map<String, Object>>> getCourierEfficiency(
            @Parameter(description = "返回条数", example = "10") @RequestParam(defaultValue = "10") int limit) {
        return Result.success(adminAnalyticsService.getCourierEfficiency(limit));
    }

    @Operation(summary = "获取总览统计数据")
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverallStats() {
        return Result.success(adminAnalyticsService.getOverallStats());
    }
}
