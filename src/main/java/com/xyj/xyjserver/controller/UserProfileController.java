package com.xyj.xyjserver.controller;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.common.api.Result;
import com.xyj.xyjserver.common.interceptor.AuthInterceptor;
import com.xyj.xyjserver.dto.AddressDTO;
import com.xyj.xyjserver.service.UserProfileService;
import com.xyj.xyjserver.vo.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "UserProfile 接口")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    /**
     * 获取用户资料首页聚合数据
     */
    @Operation(summary = "获取用户资料首页聚合数据")
    @GetMapping("/profile")
    public Result<UserProfileVO> getUserProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        return Result.success(userProfileService.getUserProfile(userId));
    }

    /**
     * 获取常用地址列表
     */
    @Operation(summary = "获取常用地址列表")
    @GetMapping("/addresses")
    public Result<List<AddressVO>> getAddresses(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        return Result.success(userProfileService.getAddresses(userId));
    }

    /**
     * 新增常用地址
     */
    @Operation(summary = "新增常用地址")
    @PostMapping("/addresses")
    public Result<AddressVO> addAddress(
            HttpServletRequest request,
            @Validated @RequestBody AddressDTO addressDTO) {
        Long userId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        return Result.success(userProfileService.addAddress(userId, addressDTO));
    }

    /**
     * 获取优惠券列表
     */
    @Operation(summary = "获取优惠券列表")
    @GetMapping("/coupons")
    public Result<PageResult<CouponVO>> getCoupons(
            HttpServletRequest request,
            @RequestParam(defaultValue = "AVAILABLE") String status,
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size) {
        Long userId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        return Result.success(userProfileService.getCoupons(userId, status, page, size));
    }

    /**
     * 获取钱包流水
     */
    @Operation(summary = "获取钱包流水")
    @GetMapping("/wallet/transactions")
    public Result<PageResult<WalletTransactionVO>> getWalletTransactions(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size) {
        Long userId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        return Result.success(userProfileService.getWalletTransactions(userId, page, size));
    }

    /**
     * 获取积分商城权益
     */
    @Operation(summary = "获取积分商城权益")
    @GetMapping("/mall/items")
    public Result<PageResult<MallItemVO>> getMallItems(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "20") Long size) {
        return Result.success(userProfileService.getMallItems(page, size));
    }

    /**
     * 兑换积分商城权益
     */
    @Operation(summary = "兑换积分商城权益")
    @PostMapping("/mall/items/{item_id}/redeem")
    public Result<RedeemRecordVO> redeemMallItem(
            HttpServletRequest request,
            @PathVariable("item_id") Long itemId) {
        Long userId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        return Result.success(userProfileService.redeemMallItem(userId, itemId));
    }

    /**
     * 获取兑换记录
     */
    @Operation(summary = "获取兑换记录")
    @GetMapping("/mall/redeem-records")
    public Result<PageResult<RedeemRecordVO>> getRedeemRecords(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size) {
        Long userId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        return Result.success(userProfileService.getRedeemRecords(userId, page, size));
    }

    /**
     * 获取帮助中心
     */
    @Operation(summary = "获取帮助中心")
    @GetMapping("/help-center")
    public Result<List<HelpItemVO>> getHelpCenter() {
        return Result.success(userProfileService.getHelpCenter());
    }

    /**
     * 获取客服信息
     */
    @Operation(summary = "获取客服信息")
    @GetMapping("/customer-service")
    public Result<CustomerServiceVO> getCustomerService() {
        return Result.success(userProfileService.getCustomerService());
    }
}