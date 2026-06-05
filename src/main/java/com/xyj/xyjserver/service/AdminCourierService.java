package com.xyj.xyjserver.service;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.dto.CreateCourierDTO;
import com.xyj.xyjserver.entity.Courier;
import com.xyj.xyjserver.vo.CourierListVO;

public interface AdminCourierService {
    PageResult<CourierListVO> getCourierList(Long page, Long size, String keyword);
    Courier createCourier(CreateCourierDTO dto);
    Courier updateCourier(Long id, Courier courier);
    void toggleCourierStatus(Long id, Integer status);
    CourierListVO getCourierDetail(Long id);
}
