package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.common.api.ResultCode;
import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.common.exception.BusinessException;
import com.xyj.xyjserver.dto.PackageComplainDTO;
import com.xyj.xyjserver.dto.PackageRateDTO;
import com.xyj.xyjserver.dto.UserPackageCreateDTO;
import com.xyj.xyjserver.mapper.UserPackageMapper;
import com.xyj.xyjserver.service.UserPackageService;
import com.xyj.xyjserver.vo.PackageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.math.BigDecimal;

@Service
public class UserPackageServiceImpl implements UserPackageService {

    @Autowired
    private UserPackageMapper userPackageMapper;

    @Override
    public PageResult<PackageVO> getMyReceivePackages(Long userId, String type, Long page, Long size) {
        Long safePage = page == null || page < 1 ? 1L : page;
        Long safeSize = size == null || size < 1 ? 10L : size;
        Long offset = (safePage - 1) * safeSize;
        List<PackageVO> records = userPackageMapper.findUserPackages(userId, offset, safeSize);
        Long total = userPackageMapper.countUserPackages(userId);
        return new PageResult<>(records, total, safeSize, safePage);
    }

    @Override
    public PackageVO createPackage(Long userId, UserPackageCreateDTO createDTO) {
        Long stationId = userPackageMapper.findDefaultStationId();
        if (stationId == null) {
            throw new BusinessException(ResultCode.FAILED, "暂无可用驿站，无法提交包裹");
        }
        String packageNo = "PKG-" + System.currentTimeMillis();
        String pickupCode = "QJ" + String.valueOf(System.currentTimeMillis()).substring(8);
        BigDecimal rewardAmount = createDTO.getRewardAmount() == null
                ? new BigDecimal("8.00")
                : createDTO.getRewardAmount();
        userPackageMapper.insertUserPackage(
                packageNo,
                pickupCode,
                createDTO.getName(),
                createDTO.getSenderName(),
                userId,
                createDTO.getReceiverName(),
                createDTO.getReceiverPhone(),
                createDTO.getAddress(),
                createDTO.getWeight() == null ? 0D : createDTO.getWeight(),
                rewardAmount,
                stationId,
                createDTO.getLat(),
                createDTO.getLng()
        );
        Long packageId = userPackageMapper.findOwnedPackageId(userId, packageNo);
        if (packageId != null) {
            userPackageMapper.insertTimeline(packageId, "PENDING_INBOUND", "用户提交包裹信息，等待站点管理员审批");
        }
        return userPackageMapper.findUserPackageByNo(userId, packageNo);
    }

    @Override
    public PackageVO getPackageDetail(Long userId, String packageId) {
        PackageVO pkg = userPackageMapper.findUserPackageByNo(userId, packageId);
        if (pkg == null) {
            throw new BusinessException(ResultCode.FORBIDDEN, "包裹不存在或不属于当前用户");
        }
        return pkg;
    }

    @Override
    public Boolean confirmReceipt(Long userId, String packageId) {
        int updated = userPackageMapper.confirmReceipt(userId, packageId);
        if (updated <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "包裹不存在、状态不可签收或不属于当前用户");
        }
        Long ownedPackageId = userPackageMapper.findOwnedPackageId(userId, packageId);
        if (ownedPackageId != null) {
            userPackageMapper.insertTimeline(ownedPackageId, "COMPLETED", "用户确认签收");
        }
        return true;
    }

    @Override
    public Boolean ratePackage(Long userId, String packageId, PackageRateDTO rateDTO) {
        ensureOwnedPackage(userId, packageId);
        return true;
    }

    @Override
    public Boolean complainPackage(Long userId, String packageId, PackageComplainDTO complainDTO) {
        ensureOwnedPackage(userId, packageId);
        return true;
    }

    private void ensureOwnedPackage(Long userId, String packageId) {
        if (userPackageMapper.findOwnedPackageId(userId, packageId) == null) {
            throw new BusinessException(ResultCode.FORBIDDEN, "包裹不存在或不属于当前用户");
        }
    }
}
