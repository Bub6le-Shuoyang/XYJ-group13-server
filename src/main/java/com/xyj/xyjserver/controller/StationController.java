package com.xyj.xyjserver.controller;

import com.xyj.xyjserver.common.api.Result;
import com.xyj.xyjserver.service.StationService;
import com.xyj.xyjserver.vo.StationVO;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stations")
@Tag(name = "Station 接口")
public class StationController {

    @Autowired
    private StationService stationService;

    /**
     * 获取附近驿站
     */
    @Operation(summary = "获取附近驿站")
    @GetMapping("/nearby")
    public Result<List<StationVO>> getNearbyStations(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng) {
        return Result.success(stationService.getNearbyStations(lat, lng));
    }

    /**
     * 获取驿站详情
     */
    @Operation(summary = "获取驿站详情")
    @GetMapping("/{station_id}")
    public Result<StationVO> getStationDetail(@PathVariable("station_id") String stationId) {
        return Result.success(stationService.getStationDetail(stationId));
    }
}