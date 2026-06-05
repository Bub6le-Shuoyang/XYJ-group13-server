package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.mapper.WalletTransactionMapper;
import com.xyj.xyjserver.service.AdminWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AdminWalletServiceImpl implements AdminWalletService {

    @Autowired
    private WalletTransactionMapper walletTransactionMapper;

    @Override
    public PageResult<Map<String, Object>> getTransactions(Long page, Long size, Long userId, String type) {
        long safePage = page == null || page < 1 ? 1L : page;
        long safeSize = size == null || size < 1 ? 10L : size;
        long offset = (safePage - 1) * safeSize;
        List<Map<String, Object>> records = walletTransactionMapper.findAll(offset, safeSize, userId, type);
        long total = walletTransactionMapper.countAll(userId, type);
        return new PageResult<>(records, total, safeSize, safePage);
    }
}
