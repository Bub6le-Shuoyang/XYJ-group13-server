package com.xyj.xyjserver.service;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.entity.AdminOperationLog;

public interface AdminOperationLogService {
    void log(Long adminId, String adminName, String operation, String targetType, Long targetId, String detail, String ip);
    PageResult<AdminOperationLog> getLogs(Long page, Long size, String operation);
}
