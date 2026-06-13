package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.common.api.ResultCode;
import com.xyj.xyjserver.common.exception.BusinessException;
import com.xyj.xyjserver.dto.UpdateAvatarDTO;
import com.xyj.xyjserver.entity.MallItem;
import com.xyj.xyjserver.entity.MallRedeemRecord;
import com.xyj.xyjserver.entity.User;
import com.xyj.xyjserver.entity.UserPointsAccount;
import com.xyj.xyjserver.mapper.MallItemMapper;
import com.xyj.xyjserver.mapper.MallRedeemRecordMapper;
import com.xyj.xyjserver.mapper.UserMapper;
import com.xyj.xyjserver.mapper.UserPointsAccountMapper;
import com.xyj.xyjserver.vo.RedeemRecordVO;
import com.xyj.xyjserver.vo.UserProfileVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceImplTest {

    @Mock
    private MallItemMapper mallItemMapper;
    @Mock
    private MallRedeemRecordMapper mallRedeemRecordMapper;
    @Mock
    private UserPointsAccountMapper userPointsAccountMapper;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    @Test
    void getUserProfile_userNotFound_shouldThrow() {
        when(userMapper.findById(1L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> userProfileService.getUserProfile(1L));
    }

    @Test
    void getUserProfile_success_shouldReturnProfileWithPoints() {
        User user = buildUser(1L);
        UserPointsAccount account = buildAccount(1L, 500, 3);
        when(userMapper.findById(1L)).thenReturn(user);
        when(userPointsAccountMapper.findByUserId(1L)).thenReturn(account);

        UserProfileVO profile = userProfileService.getUserProfile(1L);

        assertEquals("U001", profile.getUserNo());
        assertEquals(500, profile.getPoints());
        assertEquals(new BigDecimal("100.00"), profile.getBalance());
    }

    @Test
    void updateAvatar_emptyUrl_shouldThrow() {
        UpdateAvatarDTO dto = new UpdateAvatarDTO();
        dto.setAvatarUrl("  ");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> userProfileService.updateAvatar(1L, dto));
        assertEquals(ResultCode.VALIDATE_FAILED.getCode(), ex.getErrorCode().getCode());
    }

    @Test
    void redeemMallItem_itemNotFound_shouldThrow() {
        when(mallItemMapper.findById(99L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> userProfileService.redeemMallItem(1L, 99L));
    }

    @Test
    void redeemMallItem_insufficientPoints_shouldThrow() {
        MallItem item = buildMallItem(1L, 100, 10);
        UserPointsAccount account = buildAccount(1L, 50, 0);
        when(mallItemMapper.findById(1L)).thenReturn(item);
        when(userPointsAccountMapper.findByUserId(1L)).thenReturn(account);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> userProfileService.redeemMallItem(1L, 1L));
        assertTrue(ex.getMessage().contains("积分不足"));
    }

    @Test
    void redeemMallItem_success_shouldDeductPointsAndCreateRecord() {
        MallItem item = buildMallItem(1L, 100, 5);
        item.setType("coupon");
        UserPointsAccount account = buildAccount(1L, 200, 1);
        when(mallItemMapper.findById(1L)).thenReturn(item);
        when(userPointsAccountMapper.findByUserId(1L)).thenReturn(account);
        when(mallItemMapper.decreaseStock(1L)).thenReturn(1);
        when(userPointsAccountMapper.deductPoints(1L, 100)).thenReturn(1);
        doAnswer(invocation -> {
            MallRedeemRecord record = invocation.getArgument(0);
            record.setId(20L);
            return 1;
        }).when(mallRedeemRecordMapper).insert(any(MallRedeemRecord.class));

        RedeemRecordVO vo = userProfileService.redeemMallItem(1L, 1L);

        verify(userPointsAccountMapper).increaseCouponCount(1L);
        assertEquals("测试商品", vo.getItemName());
        assertEquals(100, vo.getPointsCost());
        assertEquals(100, vo.getRemainPoints());
    }

    @Test
    void getRedeemRecords_shouldMapRecords() {
        MallRedeemRecord record = new MallRedeemRecord();
        record.setId(1L);
        record.setItemName("积分礼品");
        record.setPointsCost(50);
        record.setRemainPoints(150);
        record.setStatus("COMPLETED");
        when(mallRedeemRecordMapper.countByUserId(1L)).thenReturn(1L);
        when(mallRedeemRecordMapper.findByUserId(1L, 0L, 10L)).thenReturn(List.of(record));

        PageResult<RedeemRecordVO> result = userProfileService.getRedeemRecords(1L, 1L, 10L);

        assertEquals(1L, result.getTotal());
        assertEquals("积分礼品", result.getRecords().get(0).getItemName());
    }

    @Test
    void getHelpCenter_shouldReturnStaticContent() {
        assertEquals(1, userProfileService.getHelpCenter().size());
        assertEquals("如何取件？", userProfileService.getHelpCenter().get(0).getQuestion());
    }

    @Test
    void getCustomerService_shouldReturnContactInfo() {
        assertEquals("400-123-4567", userProfileService.getCustomerService().getPhone());
    }

    private User buildUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setUserNo("U001");
        user.setNickname("测试用户");
        return user;
    }

    private UserPointsAccount buildAccount(Long userId, int points, int couponCount) {
        UserPointsAccount account = new UserPointsAccount();
        account.setUserId(userId);
        account.setPoints(points);
        account.setCouponCount(couponCount);
        account.setBalance(new BigDecimal("100.00"));
        return account;
    }

    private MallItem buildMallItem(Long id, int points, int stock) {
        MallItem item = new MallItem();
        item.setId(id);
        item.setName("测试商品");
        item.setPoints(points);
        item.setStock(stock);
        item.setType("gift");
        return item;
    }
}
