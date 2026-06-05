package com.xyj.xyjserver.controller;

import com.xyj.xyjserver.common.api.Result;
import com.xyj.xyjserver.entity.CustomerServiceConfig;
import com.xyj.xyjserver.service.AdminCustomerServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/customer-service")
@Tag(name = "AdminCustomerService 客服配置管理接口")
public class AdminCustomerServiceController {

    @Autowired
    private AdminCustomerServiceService adminCustomerServiceService;

    @Operation(summary = "获取当前生效的客服配置")
    @GetMapping("/active")
    public Result<CustomerServiceConfig> getActive() {
        return Result.success(adminCustomerServiceService.getActive());
    }

    @Operation(summary = "客服配置列表")
    @GetMapping
    public Result<List<CustomerServiceConfig>> getAll() {
        return Result.success(adminCustomerServiceService.getAll());
    }

    @Operation(summary = "保存客服配置（新增或更新）")
    @PostMapping
    public Result<CustomerServiceConfig> save(@RequestBody CustomerServiceConfig config) {
        return Result.success(adminCustomerServiceService.save(config));
    }
}
