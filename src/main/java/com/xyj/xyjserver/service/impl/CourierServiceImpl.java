package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.dto.DeliverDTO;
import com.xyj.xyjserver.dto.VerifyPickupCodeDTO;
import com.xyj.xyjserver.service.CourierService;
import com.xyj.xyjserver.vo.CourierProfileVO;
import com.xyj.xyjserver.vo.EarningsVO;
import com.xyj.xyjserver.vo.TaskVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;

@Service
public class CourierServiceImpl implements CourierService {

    @Override
    public PageResult<TaskVO> getAvailableTasks(Long courierId, Long page, Long size) {
        TaskVO vo = new TaskVO();
        vo.setTaskId("TASK-001");
        vo.setPackageId("PKG-001");
        vo.setStatus("TASK_PUBLISHED");
        vo.setRewardAmount(new BigDecimal("8.0"));
        vo.setCreatedAt(new Date());
        return new PageResult<>(Collections.singletonList(vo), 1L, size, page);
    }

    @Override
    public PageResult<TaskVO> getMyTasks(Long courierId, String status, Long page, Long size) {
        TaskVO vo = new TaskVO();
        vo.setTaskId("TASK-002");
        vo.setPackageId("PKG-002");
        vo.setStatus(status != null ? status : "ASSIGNED");
        vo.setRewardAmount(new BigDecimal("10.0"));
        vo.setCreatedAt(new Date());
        return new PageResult<>(Collections.singletonList(vo), 1L, size, page);
    }

    @Override
    public TaskVO grabTask(Long courierId, String taskId) {
        TaskVO vo = new TaskVO();
        vo.setTaskId(taskId);
        vo.setPackageId("PKG-00X");
        vo.setStatus("ASSIGNED");
        vo.setRewardAmount(new BigDecimal("8.0"));
        vo.setCreatedAt(new Date());
        return vo;
    }

    @Override
    public TaskVO pickupTask(Long courierId, String taskId) {
        TaskVO vo = new TaskVO();
        vo.setTaskId(taskId);
        vo.setPackageId("PKG-00X");
        vo.setStatus("DELIVERING");
        vo.setRewardAmount(new BigDecimal("8.0"));
        vo.setCreatedAt(new Date());
        return vo;
    }

    @Override
    public TaskVO deliverTask(Long courierId, String taskId, DeliverDTO deliverDTO) {
        TaskVO vo = new TaskVO();
        vo.setTaskId(taskId);
        vo.setPackageId("PKG-00X");
        vo.setStatus("DELIVERED_PENDING_VERIFY"); // Or direct to COMPLETED based on biz logic
        vo.setRewardAmount(new BigDecimal("8.0"));
        vo.setCreatedAt(new Date());
        return vo;
    }

    @Override
    public TaskVO verifyPickupCode(Long courierId, String taskId, VerifyPickupCodeDTO verifyDTO) {
        TaskVO vo = new TaskVO();
        vo.setTaskId(taskId);
        vo.setPackageId("PKG-00X");
        vo.setStatus("COMPLETED");
        vo.setRewardAmount(new BigDecimal("8.0"));
        vo.setCreatedAt(new Date());
        return vo;
    }

    @Override
    public EarningsVO getEarnings(Long courierId) {
        EarningsVO vo = new EarningsVO();
        vo.setTotalEarnings(new BigDecimal("1250.50"));
        vo.setTodayEarnings(new BigDecimal("85.00"));
        vo.setCompletedTasks(150);
        vo.setTodayTasks(10);
        vo.setBalance(new BigDecimal("350.50"));
        return vo;
    }

    @Override
    public CourierProfileVO getProfile(Long courierId) {
        CourierProfileVO vo = new CourierProfileVO();
        vo.setId(courierId);
        vo.setCourierNo("C" + courierId);
        vo.setName("张师傅");
        vo.setPhone("13800138000");
        vo.setAvatarUrl("/uploads/courier.png");
        vo.setStatus("WORKING");
        vo.setStationId("ST-001");
        vo.setStationName("清河村中心驿站");
        vo.setRating(4.9);
        return vo;
    }
}