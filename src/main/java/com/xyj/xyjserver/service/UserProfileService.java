package com.xyj.xyjserver.service;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.dto.AddressDTO;
import com.xyj.xyjserver.vo.*;

import java.util.List;

public interface UserProfileService {
    UserProfileVO getUserProfile(Long userId);
    List<AddressVO> getAddresses(Long userId);
    AddressVO addAddress(Long userId, AddressDTO addressDTO);
    PageResult<CouponVO> getCoupons(Long userId, String status, Long page, Long size);
    PageResult<WalletTransactionVO> getWalletTransactions(Long userId, Long page, Long size);
    PageResult<MallItemVO> getMallItems(Long page, Long size);
    RedeemRecordVO redeemMallItem(Long userId, Long itemId);
    PageResult<RedeemRecordVO> getRedeemRecords(Long userId, Long page, Long size);
    List<HelpItemVO> getHelpCenter();
    CustomerServiceVO getCustomerService();
}