package com.xyj.xyjserver.controller;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.common.api.Result;
import com.xyj.xyjserver.entity.AdminOperationLog;
import com.xyj.xyjserver.service.AdminOperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/operation-logs")
@Tag(name = "AdminOperationLog 操作日志接口")
public class AdminOperationLogController {

    @Autowired
    private AdminOperationLogService adminOperationLogService;

    @Operation(summary = "分页查询操作日志")
    @GetMapping
    public Result<PageResult<AdminOperationLog>> getLogs(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Long page,
            @Parameter(description = "每页条数", example = "10") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "操作类型筛选") @RequestParam(required = false) String operation) {
        return Result.success(adminOperationLogService.getLogs(page, size, operation));
    }
}
