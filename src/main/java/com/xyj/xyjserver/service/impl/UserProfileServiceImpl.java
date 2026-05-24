package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.dto.AddressDTO;
import com.xyj.xyjserver.service.UserProfileService;
import com.xyj.xyjserver.vo.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class UserProfileServiceImpl implements UserProfileService {

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
        MallItemVO vo = new MallItemVO();
        vo.setId(1L);
        vo.setName("洗衣液");
        vo.setImageUrl("/uploads/xiyiye.png");
        vo.setPointsRequired(200);
        vo.setStock(100);
        return new PageResult<>(Collections.singletonList(vo), 1L, size, page);
    }

    @Override
    public RedeemRecordVO redeemMallItem(Long userId, Long itemId) {
        RedeemRecordVO vo = new RedeemRecordVO();
        vo.setId(System.currentTimeMillis());
        vo.setItemName("洗衣液");
        vo.setPointsCost(200);
        vo.setRedeemTime(new Date());
        vo.setStatus("COMPLETED");
        return vo;
    }

    @Override
    public PageResult<RedeemRecordVO> getRedeemRecords(Long userId, Long page, Long size) {
        RedeemRecordVO vo = new RedeemRecordVO();
        vo.setId(1L);
        vo.setItemName("洗洁精");
        vo.setPointsCost(150);
        vo.setRedeemTime(new Date());
        vo.setStatus("COMPLETED");
        return new PageResult<>(Collections.singletonList(vo), 1L, size, page);
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