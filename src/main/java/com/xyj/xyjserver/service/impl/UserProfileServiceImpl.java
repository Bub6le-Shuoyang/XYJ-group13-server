package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.common.api.ResultCode;
import com.xyj.xyjserver.common.exception.BusinessException;
import com.xyj.xyjserver.dto.AddressDTO;
import com.xyj.xyjserver.entity.MallItem;
import com.xyj.xyjserver.entity.MallRedeemRecord;
import com.xyj.xyjserver.entity.UserPointsAccount;
import com.xyj.xyjserver.mapper.MallItemMapper;
import com.xyj.xyjserver.mapper.MallRedeemRecordMapper;
import com.xyj.xyjserver.mapper.UserPointsAccountMapper;
import com.xyj.xyjserver.service.UserProfileService;
import com.xyj.xyjserver.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    @Autowired
    private MallItemMapper mallItemMapper;

    @Autowired
    private MallRedeemRecordMapper mallRedeemRecordMapper;

    @Autowired
    private UserPointsAccountMapper userPointsAccountMapper;

    @Override
    public UserProfileVO getUserProfile(Long userId) {
        UserProfileVO vo = new UserProfileVO();
        vo.setId(userId);
        vo.setUserNo("U" + userId);
        vo.setNickname("用户" + userId);
        vo.setAvatarUrl("/uploads/avatar.png");
        vo.setPhone("13800138000");
        vo.setIsRealnameAuth(true);
        vo.setBalance(new BigDecimal("100.00"));
        vo.setPoints(500);
        vo.setCouponCount(3);
        return vo;
    }

    @Override
    public List<AddressVO> getAddresses(Long userId) {
        AddressVO vo = new AddressVO();
        vo.setId(1L);
        vo.setName("张三");
        vo.setPhone("13800001111");
        vo.setAddress("清河村 5 组 9 号");
        vo.setIsDefault(true);
        return Collections.singletonList(vo);
    }

    @Override
    public AddressVO addAddress(Long userId, AddressDTO addressDTO) {
        AddressVO vo = new AddressVO();
        vo.setId(System.currentTimeMillis());
        vo.setName(addressDTO.getName());
        vo.setPhone(addressDTO.getPhone());
        vo.setAddress(addressDTO.getAddress());
        vo.setIsDefault(addressDTO.getIsDefault());
        return vo;
    }

    @Override
    public PageResult<CouponVO> getCoupons(Long userId, String status, Long page, Long size) {
        CouponVO vo = new CouponVO();
        vo.setId(1L);
        vo.setName("新人专享券");
        vo.setDiscountAmount(new BigDecimal("5.00"));
        vo.setMinSpend(new BigDecimal("20.00"));
        vo.setStatus(status);
        vo.setValidUntil(new Date(System.currentTimeMillis() + 86400000L * 7));
        return new PageResult<>(Collections.singletonList(vo), 1L, size, page);
    }

    @Override
    public PageResult<WalletTransactionVO> getWalletTransactions(Long userId, Long page, Long size) {
        WalletTransactionVO vo = new WalletTransactionVO();
        vo.setId(1L);
        vo.setTitle("充值");
        vo.setType("INCOME");
        vo.setAmount(new BigDecimal("50.00"));
        vo.setTime(new Date());
        return new PageResult<>(Collections.singletonList(vo), 1L, size, page);
    }

    @Override
    public PageResult<MallItemVO> getMallItems(Long page, Long size) {
        long offset = (page - 1) * size;
        long total = mallItemMapper.countAvailableItems();
        List<MallItem> items = mallItemMapper.findAvailableItems(offset, size);
        
        List<MallItemVO> voList = items.stream().map(item -> {
            MallItemVO vo = new MallItemVO();
            vo.setId(item.getId());
            vo.setName(item.getName());
            vo.setImageUrl(item.getImageUrl());
            vo.setPointsRequired(item.getPoints());
            vo.setStock(item.getStock());
            return vo;
        }).collect(Collectors.toList());
        
        return new PageResult<>(voList, total, size, page);
    }

    @Override
    @Transactional
    public RedeemRecordVO redeemMallItem(Long userId, Long itemId) {
        // 1. 查找商品
        MallItem item = mallItemMapper.findById(itemId);
        if (item == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "商品不存在或已下架");
        }
        if (item.getStock() <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "商品库存不足");
        }

        // 2. 查找用户积分账户
        UserPointsAccount account = userPointsAccountMapper.findByUserId(userId);
        if (account == null) {
            // 如果账户不存在，初始化一个空账户（实际应在注册时初始化）
            userPointsAccountMapper.insertDefaultAccount(userId);
            account = userPointsAccountMapper.findByUserId(userId);
        }

        // 3. 判断积分是否足够
        if (account.getPoints() < item.getPoints()) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "积分不足");
        }

        // 4. 扣减库存 (乐观锁防止超卖)
        int updated = mallItemMapper.decreaseStock(itemId);
        if (updated == 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "商品已被抢空");
        }

        // 5. 扣减积分
        int pointsUpdated = userPointsAccountMapper.deductPoints(userId, item.getPoints());
        if (pointsUpdated == 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "积分扣减失败");
        }

        // 6. 生成兑换记录
        MallRedeemRecord record = new MallRedeemRecord();
        record.setRecordNo("RD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4));
        record.setUserId(userId);
        record.setItemId(itemId);
        record.setItemName(item.getName());
        record.setPointsCost(item.getPoints());
        record.setRemainPoints(account.getPoints() - item.getPoints());
        record.setStatus("COMPLETED");
        mallRedeemRecordMapper.insert(record);

        // 7. 返回结果
        RedeemRecordVO vo = new RedeemRecordVO();
        vo.setId(record.getId());
        vo.setItemName(record.getItemName());
        vo.setPointsCost(record.getPointsCost());
        vo.setRedeemTime(new Date());
        vo.setStatus(record.getStatus());
        return vo;
    }

    @Override
    public PageResult<RedeemRecordVO> getRedeemRecords(Long userId, Long page, Long size) {
        long offset = (page - 1) * size;
        long total = mallRedeemRecordMapper.countByUserId(userId);
        List<MallRedeemRecord> records = mallRedeemRecordMapper.findByUserId(userId, offset, size);

        List<RedeemRecordVO> voList = records.stream().map(record -> {
            RedeemRecordVO vo = new RedeemRecordVO();
            vo.setId(record.getId());
            vo.setItemName(record.getItemName());
            vo.setPointsCost(record.getPointsCost());
            vo.setRedeemTime(record.getCreatedAt());
            vo.setStatus(record.getStatus());
            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(voList, total, size, page);
    }

    @Override
    public List<HelpItemVO> getHelpCenter() {
        HelpItemVO vo = new HelpItemVO();
        vo.setId(1L);
        vo.setQuestion("如何取件？");
        vo.setAnswer("凭借取件码前往对应驿站取件。");
        return Collections.singletonList(vo);
    }

    @Override
    public CustomerServiceVO getCustomerService() {
        CustomerServiceVO vo = new CustomerServiceVO();
        vo.setPhone("400-123-4567");
        vo.setWorkTime("09:00-18:00");
        vo.setWechatId("xyj_support");
        return vo;
    }
}