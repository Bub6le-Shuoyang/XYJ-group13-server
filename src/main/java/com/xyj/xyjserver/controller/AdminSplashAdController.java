package com.xyj.xyjserver.controller;

import com.xyj.xyjserver.common.api.Result;
import com.xyj.xyjserver.entity.SplashAd;
import com.xyj.xyjserver.service.AdminSplashAdService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/splash-ads")
@Tag(name = "AdminSplashAd 开屏广告管理接口")
public class AdminSplashAdController {

    @Autowired
    private AdminSplashAdService adminSplashAdService;

    @Operation(summary = "开屏广告列表")
    @GetMapping
    public Result<List<SplashAd>> getAll() {
        return Result.success(adminSplashAdService.getAll());
    }

    @Operation(summary = "创建开屏广告")
    @PostMapping
    public Result<SplashAd> create(@RequestBody SplashAd ad) {
        return Result.success(adminSplashAdService.create(ad));
    }

    @Operation(summary = "更新开屏广告")
    @PutMapping("/{id}")
    public Result<SplashAd> update(
            @Parameter(description = "广告ID", example = "1") @PathVariable("id") Long id,
            @RequestBody SplashAd ad) {
        return Result.success(adminSplashAdService.update(id, ad));
    }

    @Operation(summary = "删除开屏广告")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "广告ID", example = "1") @PathVariable("id") Long id) {
        adminSplashAdService.delete(id);
        return Result.success();
    }
}
