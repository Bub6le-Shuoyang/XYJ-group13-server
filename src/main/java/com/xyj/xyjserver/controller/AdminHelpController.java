package com.xyj.xyjserver.controller;

import com.xyj.xyjserver.common.api.Result;
import com.xyj.xyjserver.entity.HelpItem;
import com.xyj.xyjserver.service.AdminHelpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/help")
@Tag(name = "AdminHelp 帮助中心管理接口")
public class AdminHelpController {

    @Autowired
    private AdminHelpService adminHelpService;

    @Operation(summary = "帮助项列表")
    @GetMapping
    public Result<List<HelpItem>> getAll() {
        return Result.success(adminHelpService.getAll());
    }

    @Operation(summary = "创建帮助项")
    @PostMapping
    public Result<HelpItem> create(@RequestBody HelpItem item) {
        return Result.success(adminHelpService.create(item));
    }

    @Operation(summary = "更新帮助项")
    @PutMapping("/{id}")
    public Result<HelpItem> update(
            @Parameter(description = "帮助项ID", example = "1") @PathVariable("id") Long id,
            @RequestBody HelpItem item) {
        return Result.success(adminHelpService.update(id, item));
    }

    @Operation(summary = "删除帮助项")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "帮助项ID", example = "1") @PathVariable("id") Long id) {
        adminHelpService.delete(id);
        return Result.success();
    }
}
