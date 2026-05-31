package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.common.api.ResultCode;
import com.xyj.xyjserver.common.exception.BusinessException;
import com.xyj.xyjserver.dto.DeliverDTO;
import com.xyj.xyjserver.dto.VerifyPickupCodeDTO;
import com.xyj.xyjserver.mapper.CourierTaskMapper;
import com.xyj.xyjserver.service.CourierService;
import com.xyj.xyjserver.vo.CourierProfileVO;
import com.xyj.xyjserver.vo.EarningsVO;
import com.xyj.xyjserver.vo.TaskVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CourierServiceImpl implements CourierService {

    @Autowired
    private CourierTaskMapper courierTaskMapper;

    @Override
    public PageResult<TaskVO> getAvailableTasks(Long courierId, Long page, Long size) {
        Long safePage = page == null || page < 1 ? 1L : page;
        Long safeSize = size == null || size < 1 ? 10L : size;
        Long offset = (safePage - 1) * safeSize;
        List<TaskVO> records = courierTaskMapper.findAvailableTasks(offset, safeSize);
        Long total = courierTaskMapper.countAvailableTasks();
        return new PageResult<>(records, total, safeSize, safePage);
    }

    @Override
    public PageResult<TaskVO> getMyTasks(Long courierId, String status, Long page, Long size) {
        Long safePage = page == null || page < 1 ? 1L : page;
        Long safeSize = size == null || size < 1 ? 10L : size;
        Long offset = (safePage - 1) * safeSize;
        String safeStatus = status == null || "ALL".equalsIgnoreCase(status) ? null : status;
        List<TaskVO> records = courierTaskMapper.findCourierTasks(courierId, safeStatus, offset, safeSize);
        Long total = courierTaskMapper.countCourierTasks(courierId, safeStatus);
        return new PageResult<>(records, total, safeSize, safePage);
    }

    @Override
    public TaskVO grabTask(Long courierId, String taskId) {
        int updated = courierTaskMapper.grabTask(courierId, taskId);
        if (updated <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "任务不存在或已被其他骑手抢单");
        }
        courierTaskMapper.assignPackage(courierId, taskId);
        return courierTaskMapper.findTaskByNo(taskId);
    }

    @Override
    public TaskVO pickupTask(Long courierId, String taskId) {
        int updated = courierTaskMapper.pickupTask(courierId, taskId);
        if (updated <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "任务不存在、状态不可取件或不属于当前骑手");
        }
        return courierTaskMapper.findTaskByNo(taskId);
    }

    @Override
    public TaskVO deliverTask(Long courierId, String taskId, DeliverDTO deliverDTO) {
        int updated = courierTaskMapper.deliverTask(
                courierId,
                taskId,
                deliverDTO.getDeliverImage(),
                deliverDTO.getRemark()
        );
        if (updated <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "任务不存在、状态不可送达或不属于当前骑手");
        }
        return courierTaskMapper.findTaskByNo(taskId);
    }

    @Override
    public TaskVO verifyPickupCode(Long courierId, String taskId, VerifyPickupCodeDTO verifyDTO) {
        int updated = courierTaskMapper.verifyPickupCode(courierId, taskId, verifyDTO.getPickupCode());
        if (updated <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "取件码错误、任务不存在或不属于当前骑手");
        }
        courierTaskMapper.insertEarningIfAbsent(courierId, taskId);
        return courierTaskMapper.findTaskByNo(taskId);
    }

    @Override
    public EarningsVO getEarnings(Long courierId) {
        EarningsVO vo = new EarningsVO();
        BigDecimal total = courierTaskMapper.sumTotalEarnings(courierId);
        BigDecimal today = courierTaskMapper.sumTodayEarnings(courierId);
        vo.setTotalEarnings(total);
        vo.setTodayEarnings(today);
        vo.setCompletedTasks(courierTaskMapper.countCompletedTasks(courierId));
        vo.setTodayTasks(courierTaskMapper.countTodayTasks(courierId));
        vo.setBalance(total);
        return vo;
    }

    @Override
    public CourierProfileVO getProfile(Long courierId) {
        CourierProfileVO vo = courierTaskMapper.findCourierProfile(courierId);
        if (vo == null) {
            throw new BusinessException(ResultCode.FORBIDDEN, "配送员不存在");
        }
        return vo;
    }
}
