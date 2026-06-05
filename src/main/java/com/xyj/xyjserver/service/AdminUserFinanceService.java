package com.xyj.xyjserver.service;

import com.xyj.xyjserver.common.api.PageResult;

import java.math.BigDecimal;
import java.util.Map;

public interface AdminUserFinanceService {
    PageResult<Map<String, Object>> getAccounts(Long page, Long size);
    void adjustPoints(Long userId, int points);
    void adjustBalance(Long userId, BigDecimal amount);
    void issueCoupon(Long userId, String name, BigDecimal amount, String source);
}
