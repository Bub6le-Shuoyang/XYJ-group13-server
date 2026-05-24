package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.dto.PackageInboundDTO;
import com.xyj.xyjserver.dto.TaskPublishDTO;
import com.xyj.xyjserver.service.AdminPackageService;
import com.xyj.xyjserver.vo.PackageVO;
import com.xyj.xyjserver.vo.StationStatisticsVO;
import com.xyj.xyjserver.vo.TaskVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

@Service
public class AdminPackageServiceImpl implements AdminPackageService {

    @Override
    public PageResult<PackageVO> getStationPackages(Long adminId, String status, Long page, Long size) {
        PackageVO pkg = new PackageVO();
        pkg.setPackageId("PKG-001");
        pkg.setName("生活用品");
        pkg.setReceiverName("李爷爷");
        pkg.setReceiverPhone("13900002222");
        pkg.setAddress("清河村 3 组");
        pkg.setWeight(2.5);
        pkg.setEstimatedFee(BigDecimal.ZERO);
        pkg.setStatus(status != null ? status : "IN_STOCK");
        pkg.setPickupCode("QJ25001");
        pkg.setSenderName("镇超市");
        pkg.setTimeline(Arrays.asList("包裹到达驿站", "驿站入库完成，货架号 A-01"));
        pkg.setStationId("ST-001");
        pkg.setStationName("清河村中心驿站");
        return new PageResult<>(Collections.singletonList(pkg), 1L, size, page);
    }

    @Override
    public Boolean inboundPackage(Long adminId, String packageId, PackageInboundDTO inboundDTO) {
        // Mock: update package status to IN_STOCK and set shelf_number
        return true;
    }

    @Override
    public Boolean outboundPackage(Long adminId, String packageId) {
        // Mock: update package status to COMPLETED
        return true;
    }

    @Override
    public TaskVO publishTask(Long adminId, TaskPublishDTO publishDTO) {
        // Mock: create task, change package status to TASK_PUBLISHED
        TaskVO vo = new TaskVO();
        vo.setTaskId("TASK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        vo.setPackageId(publishDTO.getPackageId());
        vo.setStatus("PUBLISHED");
        vo.setRewardAmount(publishDTO.getRewardAmount());
        vo.setCreatedAt(new Date());
        return vo;
    }

    @Override
    public StationStatisticsVO getStationStatistics(Long adminId) {
        StationStatisticsVO vo = new StationStatisticsVO();
        vo.setPendingInboundCount(12);
        vo.setInStockCount(45);
        vo.setDeliveringCount(8);
        vo.setCompletedCount(120);
        vo.setTodayInbound(30);
        vo.setTodayOutbound(25);
        return vo;
    }
}