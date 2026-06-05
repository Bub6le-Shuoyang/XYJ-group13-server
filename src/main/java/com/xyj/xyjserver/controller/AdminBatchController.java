package com.xyj.xyjserver.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.xyj.xyjserver.common.api.Result;
import com.xyj.xyjserver.common.api.ResultCode;
import com.xyj.xyjserver.common.exception.BusinessException;
import com.xyj.xyjserver.common.interceptor.AuthInterceptor;
import com.xyj.xyjserver.dto.PackageApproveDTO;
import com.xyj.xyjserver.dto.PackageInboundDTO;
import com.xyj.xyjserver.service.AdminPackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/batch")
@Tag(name = "AdminBatch 批量操作接口")
public class AdminBatchController {

    @Autowired
    private AdminPackageService adminPackageService;

    @Operation(summary = "批量入库")
    @PostMapping("/inbound")
    public Result<Map<String, Object>> batchInbound(
            HttpServletRequest request,
            @RequestBody BatchInboundRequest body) {
        Long adminId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        requireAdmin(request);

        List<String> ids = body.getIds();
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "包裹ID列表不能为空");
        }

        String prefix = body.getShelfNumberPrefix() != null ? body.getShelfNumberPrefix() : "A-";
        int success = 0;
        int failed = 0;
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < ids.size(); i++) {
            String packageId = ids.get(i);
            try {
                PackageInboundDTO dto = new PackageInboundDTO();
                dto.setShelfNumber(prefix + (i + 1));
                adminPackageService.inboundPackage(adminId, packageId, dto);
                success++;
            } catch (Exception e) {
                failed++;
                errors.add("ID " + packageId + ": " + e.getMessage());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("failed", failed);
        result.put("errors", errors);
        return Result.success(result);
    }

    @Operation(summary = "批量审批")
    @PostMapping("/approve")
    public Result<Map<String, Object>> batchApprove(
            HttpServletRequest request,
            @RequestBody BatchApproveRequest body) {
        Long adminId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        requireAdmin(request);

        List<String> ids = body.getIds();
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "包裹ID列表不能为空");
        }

        BigDecimal rewardAmount = body.getRewardAmount() != null ? body.getRewardAmount() : new BigDecimal("8.00");
        int success = 0;
        int failed = 0;
        List<String> errors = new ArrayList<>();

        for (String packageId : ids) {
            try {
                PackageApproveDTO dto = new PackageApproveDTO();
                dto.setRewardAmount(rewardAmount);
                adminPackageService.approvePackage(adminId, packageId, dto);
                success++;
            } catch (Exception e) {
                failed++;
                errors.add("ID " + packageId + ": " + e.getMessage());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("failed", failed);
        result.put("errors", errors);
        return Result.success(result);
    }

    private void requireAdmin(HttpServletRequest request) {
        String role = (String) request.getAttribute(AuthInterceptor.USER_ROLE_ATTR);
        if (!"ADMIN".equals(role)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "当前角色无权访问管理员接口");
        }
    }

    @Data
    public static class BatchInboundRequest {
        private List<String> ids;

        @JsonProperty("shelf_number_prefix")
        private String shelfNumberPrefix;
    }

    @Data
    public static class BatchApproveRequest {
        private List<String> ids;

        @JsonProperty("reward_amount")
        private BigDecimal rewardAmount;
    }
}
