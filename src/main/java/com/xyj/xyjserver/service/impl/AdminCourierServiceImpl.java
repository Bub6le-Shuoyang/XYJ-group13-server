package com.xyj.xyjserver.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.common.api.ResultCode;
import com.xyj.xyjserver.common.exception.BusinessException;
import com.xyj.xyjserver.dto.CreateCourierDTO;
import com.xyj.xyjserver.entity.Courier;
import com.xyj.xyjserver.mapper.CourierMapper;
import com.xyj.xyjserver.service.AdminCourierService;
import com.xyj.xyjserver.vo.CourierListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminCourierServiceImpl implements AdminCourierService {

    @Autowired
    private CourierMapper courierMapper;

    @Override
    public PageResult<CourierListVO> getCourierList(Long page, Long size, String keyword) {
        long offset = (page - 1) * size;
        // 超管查看所有站点，这里 stationId 传 null
        List<Map<String, Object>> rows = courierMapper.searchByKeyword(keyword, null, offset, size);
        long total = courierMapper.countByKeyword(keyword, null);

        List<CourierListVO> voList = rows.stream().map(this::rowToVO).collect(Collectors.toList());
        return new PageResult<>(voList, total, size, page);
    }

    @Override
    public Courier createCourier(CreateCourierDTO dto) {
        // 检查账号唯一性
        long count = courierMapper.countByAccountExclude(dto.getAccount(), 0L);
        if (count > 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "账号已存在");
        }
        // 检查手机号
        if (dto.getPhone() != null && !dto.getPhone().isBlank()) {
            long pc = courierMapper.countByPhoneExclude(dto.getPhone(), 0L);
            if (pc > 0) {
                throw new BusinessException(ResultCode.VALIDATE_FAILED, "手机号已被使用");
            }
        }

        Courier courier = new Courier();
        courier.setCourierNo("COURIER-" + System.currentTimeMillis());
        courier.setAccount(dto.getAccount());
        courier.setPasswordHash(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt()));
        courier.setName(dto.getName());
        courier.setPhone(dto.getPhone());
        courier.setStationId(dto.getStationId());
        courier.setLevelName("普通配送员 Lv.1");
        courier.setLevelProgress(BigDecimal.ZERO);
        courier.setMonthlyRank(0);
        courier.setStatus(1);

        courierMapper.insert(courier);
        return courier;
    }

    @Override
    public Courier updateCourier(Long id, Courier courier) {
        Courier existing = courierMapper.findById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "配送员不存在");
        }
        if (courier.getPhone() != null && !courier.getPhone().equals(existing.getPhone())) {
            long pc = courierMapper.countByPhoneExclude(courier.getPhone(), id);
            if (pc > 0) {
                throw new BusinessException(ResultCode.VALIDATE_FAILED, "手机号已被其他配送员使用");
            }
        }
        courier.setId(id);
        courierMapper.update(courier);
        return courierMapper.findById(id);
    }

    @Override
    public void toggleCourierStatus(Long id, Integer status) {
        Courier existing = courierMapper.findById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "配送员不存在");
        }
        Courier update = new Courier();
        update.setId(id);
        update.setStatus(status);
        courierMapper.update(update);
    }

    @Override
    public CourierListVO getCourierDetail(Long id) {
        Courier existing = courierMapper.findById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "配送员不存在");
        }
        CourierListVO vo = entityToVO(existing);
        // 附加业绩数据
        vo.setTotalEarnings(courierMapper.sumTotalEarnings(id));
        vo.setCompletedTasks(courierMapper.countCompletedTasks(id));
        vo.setActiveTasks(courierMapper.countActiveTasks(id));
        return vo;
    }

    private CourierListVO rowToVO(Map<String, Object> row) {
        CourierListVO vo = new CourierListVO();
        vo.setId(((Number) row.get("id")).longValue());
        vo.setCourierNo((String) row.get("courier_no"));
        vo.setAccount((String) row.get("account"));
        vo.setName((String) row.get("name"));
        vo.setPhone((String) row.get("phone"));
        vo.setAvatarUrl((String) row.get("avatar_url"));
        Object sid = row.get("station_id");
        vo.setStationId(sid != null ? ((Number) sid).longValue() : null);
        vo.setStationName((String) row.get("station_name"));
        vo.setLevelName((String) row.get("level_name"));
        Object st = row.get("status");
        vo.setStatus(st != null ? ((Number) st).intValue() : 0);
        vo.setCreatedAt((java.util.Date) row.get("created_at"));
        return vo;
    }

    private CourierListVO entityToVO(Courier c) {
        CourierListVO vo = new CourierListVO();
        vo.setId(c.getId());
        vo.setCourierNo(c.getCourierNo());
        vo.setAccount(c.getAccount());
        vo.setName(c.getName());
        vo.setPhone(c.getPhone());
        vo.setAvatarUrl(c.getAvatarUrl());
        vo.setStationId(c.getStationId());
        vo.setLevelName(c.getLevelName());
        vo.setStatus(c.getStatus());
        vo.setCreatedAt(c.getCreatedAt());
        return vo;
    }
}
