package com.xyj.xyjserver.controller;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.common.api.Result;
import com.xyj.xyjserver.common.interceptor.AuthInterceptor;
import com.xyj.xyjserver.dto.DeliverDTO;
import com.xyj.xyjserver.dto.VerifyPickupCodeDTO;
import com.xyj.xyjserver.service.CourierService;
import com.xyj.xyjserver.vo.CourierProfileVO;
import com.xyj.xyjserver.vo.EarningsVO;
import com.xyj.xyjserver.vo.TaskVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/courier")
@Tag(name = "Courier 接口")
public class CourierController {

    @Autowired
    private CourierService courierService;

    /**
     * 获取可抢任务列表（任务大厅）
     */
    @Operation(summary = "获取可抢任务列表（任务大厅）")
    @GetMapping("/tasks/available")
    public Result<PageResult<TaskVO>> getAvailableTasks(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size) {
        Long courierId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        return Result.success(courierService.getAvailableTasks(courierId, page, size));
    }

    /**
     * 获取我的配送任务
     */
    @Operation(summary = "获取我的配送任务")
    @GetMapping("/tasks/mine")
    public Result<PageResult<TaskVO>> getMyTasks(
            HttpServletRequest request,
            @RequestParam(defaultValue = "ASSIGNED") String status,
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size) {
        Long courierId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        return Result.success(courierService.getMyTasks(courierId, status, page, size));
    }

    /**
     * 配送员抢单
     */
    @Operation(summary = "配送员抢单")
    @PostMapping("/tasks/{task_id}/grab")
    public Result<TaskVO> grabTask(
            HttpServletRequest request,
            @PathVariable("task_id") String taskId) {
        Long courierId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        return Result.success(courierService.grabTask(courierId, taskId));
    }

    /**
     * 确认取件
     */
    @Operation(summary = "确认取件")
    @PostMapping("/tasks/{task_id}/pickup")
    public Result<TaskVO> pickupTask(
            HttpServletRequest request,
            @PathVariable("task_id") String taskId) {
        Long courierId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        return Result.success(courierService.pickupTask(courierId, taskId));
    }

    /**
     * 上传送达凭证
     */
    @Operation(summary = "上传送达凭证")
    @PostMapping("/tasks/{task_id}/deliver")
    public Result<TaskVO> deliverTask(
            HttpServletRequest request,
            @PathVariable("task_id") String taskId,
            @Validated @RequestBody DeliverDTO deliverDTO) {
        Long courierId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        return Result.success(courierService.deliverTask(courierId, taskId, deliverDTO));
    }

    /**
     * 核验取件码并完成签收
     */
    @Operation(summary = "核验取件码并完成签收")
    @PostMapping("/tasks/{task_id}/verify-pickup-code")
    public Result<TaskVO> verifyPickupCode(
            HttpServletRequest request,
            @PathVariable("task_id") String taskId,
            @Validated @RequestBody VerifyPickupCodeDTO verifyDTO) {
        Long courierId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        return Result.success(courierService.verifyPickupCode(courierId, taskId, verifyDTO));
    }

    /**
     * 查看收益
     */
    @Operation(summary = "查看收益")
    @GetMapping("/earnings")
    public Result<EarningsVO> getEarnings(HttpServletRequest request) {
        Long courierId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        return Result.success(courierService.getEarnings(courierId));
    }

    /**
     * 获取配送员资料
     */
    @Operation(summary = "获取配送员资料")
    @GetMapping("/profile")
    public Result<CourierProfileVO> getProfile(HttpServletRequest request) {
        Long courierId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        return Result.success(courierService.getProfile(courierId));
    }
}