package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.common.api.ResultCode;
import com.xyj.xyjserver.common.exception.BusinessException;
import com.xyj.xyjserver.dto.PackageApproveDTO;
import com.xyj.xyjserver.dto.PackageInboundDTO;
import com.xyj.xyjserver.dto.TaskPublishDTO;
import com.xyj.xyjserver.mapper.AdminPackageMapper;
import com.xyj.xyjserver.service.AdminPackageService;
import com.xyj.xyjserver.vo.PackageVO;
import com.xyj.xyjserver.vo.StationStatisticsVO;
import com.xyj.xyjserver.vo.TaskVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.math.BigDecimal;

@Service
public class AdminPackageServiceImpl implements AdminPackageService {

    @Autowired
    private AdminPackageMapper adminPackageMapper;

    @Override
    public PageResult<PackageVO> getStationPackages(Long adminId, String status, Long page, Long size) {
        Long stationId = requireStation(adminId);
        Long safePage = page == null || page < 1 ? 1L : page;
        Long safeSize = size == null || size < 1 ? 100L : size;
        Long offset = (safePage - 1) * safeSize;
        String safeStatus = status == null || "ALL".equalsIgnoreCase(status) ? null : status;
        List<PackageVO> records = adminPackageMapper.findStationPackages(stationId, safeStatus, offset, safeSize);
        Long total = adminPackageMapper.countStationPackages(stationId, safeStatus);
        return new PageResult<>(records, total, safeSize, safePage);
    }

    @Override
    public TaskVO approvePackage(Long adminId, String packageId, PackageApproveDTO approveDTO) {
        Long stationId = requireStation(adminId);
        int updated = adminPackageMapper.approvePackage(stationId, packageId);
        if (updated <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "包裹不存在、已审批或不属于当前站点");
        }
        Long id = adminPackageMapper.findStationPackageId(stationId, packageId);
        if (id != null) {
            adminPackageMapper.insertTimeline(id, "TASK_PUBLISHED", "站点管理员审批通过，等待骑手抢单");
        }
        String taskNo = "TASK-" + System.currentTimeMillis();
        BigDecimal rewardAmount = approveDTO.getRewardAmount() == null
                ? new BigDecimal("8.00")
                : approveDTO.getRewardAmount();
        adminPackageMapper.insertTask(stationId, packageId, taskNo, rewardAmount);
        TaskVO task = adminPackageMapper.findTaskByPackageNo(stationId, packageId);
        if (task == null) {
            throw new BusinessException(ResultCode.FAILED, "审批通过但配送任务创建失败");
        }
        return task;
    }

    @Override
    public Boolean inboundPackage(Long adminId, String packageId, PackageInboundDTO inboundDTO) {
        Long stationId = requireStation(adminId);
        int updated = adminPackageMapper.inboundPackage(stationId, packageId);
        if (updated <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "包裹不存在、状态不可入库或不属于当前站点");
        }
        Long id = adminPackageMapper.findStationPackageId(stationId, packageId);
        if (id != null) {
            adminPackageMapper.insertTimeline(id, "IN_STOCK", "站点管理员确认包裹入库");
        }
        return true;
    }

    @Override
    public Boolean outboundPackage(Long adminId, String packageId) {
        Long stationId = requireStation(adminId);
        int updated = adminPackageMapper.outboundPackage(stationId, packageId);
        if (updated <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "包裹不存在、状态不可出库或不属于当前站点");
        }
        Long id = adminPackageMapper.findStationPackageId(stationId, packageId);
        if (id != null) {
            adminPackageMapper.insertTimeline(id, "TASK_PUBLISHED", "站点管理员确认包裹出库");
        }
        return true;
    }

    @Override
    public TaskVO publishTask(Long adminId, TaskPublishDTO publishDTO) {
        Long stationId = requireStation(adminId);
        adminPackageMapper.outboundPackage(stationId, publishDTO.getPackageId());
        String taskNo = "TASK-" + System.currentTimeMillis();
        adminPackageMapper.insertTask(
                stationId,
                publishDTO.getPackageId(),
                taskNo,
                publishDTO.getRewardAmount()
        );
        TaskVO task = adminPackageMapper.findTaskByPackageNo(stationId, publishDTO.getPackageId());
        if (task == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "任务发布失败，包裹不存在或已发布任务");
        }
        return task;
    }

    @Override
    public StationStatisticsVO getStationStatistics(Long adminId) {
        Long stationId = requireStation(adminId);
        StationStatisticsVO vo = adminPackageMapper.getStationStatistics(stationId);
        if (vo == null) {
            vo = new StationStatisticsVO();
            vo.setPendingInboundCount(0);
            vo.setInStockCount(0);
            vo.setDeliveringCount(0);
            vo.setCompletedCount(0);
            vo.setTodayInbound(0);
            vo.setTodayOutbound(0);
        }
        return vo;
    }

    private Long requireStation(Long adminId) {
        Long stationId = adminPackageMapper.findAdminStationId(adminId);
        if (stationId == null) {
            throw new BusinessException(ResultCode.FORBIDDEN, "当前管理员未绑定站点");
        }
        return stationId;
    }
}
