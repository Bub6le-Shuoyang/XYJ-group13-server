package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.entity.AdminOperationLog;
import com.xyj.xyjserver.mapper.AdminOperationLogMapper;
import com.xyj.xyjserver.service.AdminOperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminOperationLogServiceImpl implements AdminOperationLogService {

    @Autowired
    private AdminOperationLogMapper adminOperationLogMapper;

    @Override
    public void log(Long adminId, String adminName, String operation, String targetType, Long targetId, String detail, String ip) {
        AdminOperationLog log = new AdminOperationLog();
        log.setAdminId(adminId);
        log.setAdminName(adminName);
        log.setOperation(operation);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setDetail(detail);
        log.setIp(ip);
        adminOperationLogMapper.insert(log);
    }

    @Override
    public PageResult<AdminOperationLog> getLogs(Long page, Long size, String operation) {
        long offset = (page - 1) * size;
        List<AdminOperationLog> records = adminOperationLogMapper.findByPage(offset, size, operation);
        Long total = adminOperationLogMapper.countByFilter(operation);
        return new PageResult<>(records, total, size, page);
    }
}
