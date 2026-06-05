package com.xyj.xyjserver.service;

import com.xyj.xyjserver.common.api.PageResult;

import java.util.Map;

public interface AdminRedeemService {
    PageResult<Map<String, Object>> getRecords(Long page, Long size);
    void fulfillRecord(Long id);
    void cancelRecord(Long id);
}
