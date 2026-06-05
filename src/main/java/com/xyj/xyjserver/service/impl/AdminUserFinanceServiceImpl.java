package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.common.api.ResultCode;
import com.xyj.xyjserver.common.exception.BusinessException;
import com.xyj.xyjserver.entity.UserPointsAccount;
import com.xyj.xyjserver.mapper.CouponMapper;
import com.xyj.xyjserver.mapper.UserPointsAccountMapper;
import com.xyj.xyjserver.mapper.WalletTransactionMapper;
import com.xyj.xyjserver.service.AdminUserFinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class AdminUserFinanceServiceImpl implements AdminUserFinanceService {

    @Autowired
    private UserPointsAccountMapper userPointsAccountMapper;

    @Autowired
    private WalletTransactionMapper walletTransactionMapper;

    @Autowired
    private CouponMapper couponMapper;

    @Override
    public PageResult<Map<String, Object>> getAccounts(Long page, Long size) {
        long safePage = page == null || page < 1 ? 1L : page;
        long safeSize = size == null || size < 1 ? 10L : size;
        long offset = (safePage - 1) * safeSize;
        List<Map<String, Object>> records = userPointsAccountMapper.findAllWithUser(offset, safeSize);
        long total = userPointsAccountMapper.countAll();
        return new PageResult<>(records, total, safeSize, safePage);
    }

    @Override
    @Transactional
    public void adjustPoints(Long userId, int points) {
        UserPointsAccount account = userPointsAccountMapper.findByUserId(userId);
        if (account == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "用户积分账户不存在");
        }
        // Validate that subtraction won't make points negative
        if (points < 0 && account.getPoints() + points < 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "积分不足，调整后积分不能为负");
        }
        int rows = userPointsAccountMapper.addPoints(userId, points);
        if (rows <= 0) {
            throw new BusinessException(ResultCode.FAILED, "积分调整失败");
        }
        // Record wallet transaction
        String type = points > 0 ? "POINTS_ADD" : "POINTS_SUBTRACT";
        String desc = points > 0 ? "管理员增加积分: " + points : "管理员扣减积分: " + Math.abs(points);
        walletTransactionMapper.insert(userId, type, new BigDecimal(points), desc);
    }

    @Override
    @Transactional
    public void adjustBalance(Long userId, BigDecimal amount) {
        UserPointsAccount account = userPointsAccountMapper.findByUserId(userId);
        if (account == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "用户余额账户不存在");
        }
        // Validate that subtraction won't make balance negative
        if (amount.compareTo(BigDecimal.ZERO) < 0 && account.getBalance().add(amount).compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "余额不足，调整后余额不能为负");
        }
        int rows = userPointsAccountMapper.addBalance(userId, amount);
        if (rows <= 0) {
            throw new BusinessException(ResultCode.FAILED, "余额调整失败");
        }
        // Record wallet transaction
        String type = amount.compareTo(BigDecimal.ZERO) > 0 ? "BALANCE_ADD" : "BALANCE_SUBTRACT";
        String desc = amount.compareTo(BigDecimal.ZERO) > 0
                ? "管理员增加余额: " + amount
                : "管理员扣减余额: " + amount.abs();
        walletTransactionMapper.insert(userId, type, amount, desc);
    }

    @Override
    @Transactional
    public void issueCoupon(Long userId, String name, BigDecimal amount, String source) {
        UserPointsAccount account = userPointsAccountMapper.findByUserId(userId);
        if (account == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "用户账户不存在");
        }
        String couponNo = "CPN-" + System.currentTimeMillis();
        couponMapper.issueCoupon(userId, couponNo, name, amount, source);
        userPointsAccountMapper.increaseCouponCount(userId);
    }
}
