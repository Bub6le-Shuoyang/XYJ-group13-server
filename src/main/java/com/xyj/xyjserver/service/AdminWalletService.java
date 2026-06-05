package com.xyj.xyjserver.service;

import com.xyj.xyjserver.common.api.PageResult;

import java.util.Map;

public interface AdminWalletService {
    PageResult<Map<String, Object>> getTransactions(Long page, Long size, Long userId, String type);
}
