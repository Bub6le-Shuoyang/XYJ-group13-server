package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.common.api.ResultCode;
import com.xyj.xyjserver.common.exception.BusinessException;
import com.xyj.xyjserver.entity.MallRedeemRecord;
import com.xyj.xyjserver.mapper.MallRedeemRecordMapper;
import com.xyj.xyjserver.service.AdminRedeemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AdminRedeemServiceImpl implements AdminRedeemService {

    @Autowired
    private MallRedeemRecordMapper mallRedeemRecordMapper;

    @Override
    public PageResult<Map<String, Object>> getRecords(Long page, Long size) {
        long safePage = page == null || page < 1 ? 1L : page;
        long safeSize = size == null || size < 1 ? 10L : size;
        long offset = (safePage - 1) * safeSize;
        List<Map<String, Object>> records = mallRedeemRecordMapper.findAll(offset, safeSize);
        long total = mallRedeemRecordMapper.countAll();
        return new PageResult<>(records, total, safeSize, safePage);
    }

    @Override
    public void fulfillRecord(Long id) {
        MallRedeemRecord record = mallRedeemRecordMapper.findById(id);
        if (record == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "兑换记录不存在");
        }
        if (!"PENDING".equals(record.getStatus()) && !"SUCCESS".equals(record.getStatus())) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "仅待处理(PENDING)或已完成(SUCCESS)状态的记录可以标记为已履约");
        }
        int rows = mallRedeemRecordMapper.updateStatus(id, "FULFILLED");
        if (rows <= 0) {
            throw new BusinessException(ResultCode.FAILED, "状态更新失败");
        }
    }

    @Override
    public void cancelRecord(Long id) {
        MallRedeemRecord record = mallRedeemRecordMapper.findById(id);
        if (record == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "兑换记录不存在");
        }
        if ("FULFILLED".equals(record.getStatus())) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "已履约的记录不能取消");
        }
        int rows = mallRedeemRecordMapper.updateStatus(id, "CANCELLED");
        if (rows <= 0) {
            throw new BusinessException(ResultCode.FAILED, "状态更新失败");
        }
    }
}
