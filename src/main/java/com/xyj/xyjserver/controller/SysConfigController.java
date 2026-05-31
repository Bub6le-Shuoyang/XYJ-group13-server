package com.xyj.xyjserver.controller;

import com.xyj.xyjserver.common.api.Result;
import com.xyj.xyjserver.service.SysConfigService;
import com.xyj.xyjserver.vo.SplashAdVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sys")
@Tag(name = "SysConfig 接口", description = "系统配置与广告相关接口（无需鉴权）")
public class SysConfigController {

    @Autowired
    private SysConfigService sysConfigService;

    @Operation(summary = "获取随机开屏广告", description = "前端启动时拉取，根据权重随机返回一条上线状态的开屏广告，并存入本地缓存")
    @GetMapping("/ads/splash")
    public Result<SplashAdVO> getRandomSplashAd() {
        return Result.success(sysConfigService.getRandomSplashAd());
    }
}