package com.xyj.xyjserver.controller;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.common.api.Result;
import com.xyj.xyjserver.dto.CreateCourierDTO;
import com.xyj.xyjserver.entity.Courier;
import com.xyj.xyjserver.service.AdminCourierService;
import com.xyj.xyjserver.vo.CourierListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/couriers")
@Tag(name = "AdminCourier 配送员管理接口")
public class AdminCourierController {

    @Autowired
    private AdminCourierService adminCourierService;

    @Operation(summary = "配送员列表（分页+搜索）")
    @GetMapping
    public Result<PageResult<CourierListVO>> getCourierList(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Long page,
            @Parameter(description = "每页条数", example = "10") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "搜索关键词", example = "李四") @RequestParam(required = false) String keyword) {
        return Result.success(adminCourierService.getCourierList(page, size, keyword));
    }

    @Operation(summary = "创建配送员")
    @PostMapping
    public Result<Courier> createCourier(@Validated @RequestBody CreateCourierDTO dto) {
        return Result.success(adminCourierService.createCourier(dto));
    }

    @Operation(summary = "更新配送员信息")
    @PutMapping("/{id}")
    public Result<Courier> updateCourier(
            @Parameter(description = "配送员ID", example = "1") @PathVariable("id") Long id,
            @RequestBody Courier courier) {
        return Result.success(adminCourierService.updateCourier(id, courier));
    }

    @Operation(summary = "启用/禁用配送员")
    @PutMapping("/{id}/status")
    public Result<Void> toggleStatus(
            @Parameter(description = "配送员ID", example = "1") @PathVariable("id") Long id,
            @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        if (status == null) {
            status = 1;
        }
        adminCourierService.toggleCourierStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "配送员详情（含业绩数据）")
    @GetMapping("/{id}")
    public Result<CourierListVO> getCourierDetail(
            @Parameter(description = "配送员ID", example = "1") @PathVariable("id") Long id) {
        return Result.success(adminCourierService.getCourierDetail(id));
    }
}
