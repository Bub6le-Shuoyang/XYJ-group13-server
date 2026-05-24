package com.xyj.xyjserver.service;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.dto.DeliverDTO;
import com.xyj.xyjserver.dto.VerifyPickupCodeDTO;
import com.xyj.xyjserver.vo.CourierProfileVO;
import com.xyj.xyjserver.vo.EarningsVO;
import com.xyj.xyjserver.vo.TaskVO;

public interface CourierService {
    PageResult<TaskVO> getAvailableTasks(Long courierId, Long page, Long size);
    PageResult<TaskVO> getMyTasks(Long courierId, String status, Long page, Long size);
    TaskVO grabTask(Long courierId, String taskId);
    TaskVO pickupTask(Long courierId, String taskId);
    TaskVO deliverTask(Long courierId, String taskId, DeliverDTO deliverDTO);
    TaskVO verifyPickupCode(Long courierId, String taskId, VerifyPickupCodeDTO verifyDTO);
    EarningsVO getEarnings(Long courierId);
    CourierProfileVO getProfile(Long courierId);
}