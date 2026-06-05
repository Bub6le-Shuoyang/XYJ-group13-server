package com.xyj.xyjserver.controller;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.common.api.Result;
import com.xyj.xyjserver.dto.CreateStationDTO;
import com.xyj.xyjserver.entity.Station;
import com.xyj.xyjserver.service.AdminStationService;
import com.xyj.xyjserver.vo.StationListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/stations")
@Tag(name = "AdminStation 站点管理接口")
public class AdminStationController {

    @Autowired
    private AdminStationService adminStationService;

    @Operation(summary = "站点列表（分页+搜索）")
    @GetMapping
    public Result<PageResult<StationListVO>> getStationList(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Long page,
            @Parameter(description = "每页条数", example = "10") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "搜索关键词", example = "清河") @RequestParam(required = false) String keyword) {
        return Result.success(adminStationService.getStationList(page, size, keyword));
    }

    @Operation(summary = "创建站点")
    @PostMapping
    public Result<Station> createStation(@Validated @RequestBody CreateStationDTO dto) {
        return Result.success(adminStationService.createStation(dto));
    }

    @Operation(summary = "更新站点")
    @PutMapping("/{id}")
    public Result<Station> updateStation(
            @Parameter(description = "站点ID", example = "1") @PathVariable("id") Long id,
            @Validated @RequestBody CreateStationDTO dto) {
        return Result.success(adminStationService.updateStation(id, dto));
    }

    @Operation(summary = "删除站点（软删除）")
    @DeleteMapping("/{id}")
    public Result<Void> deleteStation(
            @Parameter(description = "站点ID", example = "1") @PathVariable("id") Long id) {
        adminStationService.deleteStation(id);
        return Result.success();
    }

    @Operation(summary = "获取所有启用站点（下拉选择用）")
    @GetMapping("/active")
    public Result<List<Station>> getActiveStations() {
        return Result.success(adminStationService.getAllActiveStations());
    }
}
