package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.dto.PackageComplainDTO;
import com.xyj.xyjserver.dto.PackageRateDTO;
import com.xyj.xyjserver.service.UserPackageService;
import com.xyj.xyjserver.vo.PackageVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class UserPackageServiceImpl implements UserPackageService {

    @Override
    public PageResult<PackageVO> getMyReceivePackages(Long userId, String type, Long page, Long size) {
        // Mock data
        PackageVO pkg = getMockPackageVO();
        List<PackageVO> records = Collections.singletonList(pkg);
        return new PageResult<>(records, 1L, size, page);
    }

    @Override
    public PackageVO getPackageDetail(Long userId, String packageId) {
        // Mock data
        PackageVO pkg = getMockPackageVO();
        pkg.setPackageId(packageId);
        return pkg;
    }

    @Override
    public Boolean confirmReceipt(Long userId, String packageId) {
        // Mock logic: update package status in DB
        return true;
    }

    @Override
    public Boolean ratePackage(Long userId, String packageId, PackageRateDTO rateDTO) {
        // Mock logic: insert rate record into DB
        return true;
    }

    @Override
    public Boolean complainPackage(Long userId, String packageId, PackageComplainDTO complainDTO) {
        // Mock logic: insert complain record into DB
        return true;
    }

    private PackageVO getMockPackageVO() {
        PackageVO pkg = new PackageVO();
        pkg.setPackageId("PKG-003");
        pkg.setTaskId("TASK-003");
        pkg.setName("药品快件");
        pkg.setReceiverName("赵奶奶");
        pkg.setReceiverPhone("13800001111");
        pkg.setAddress("清河村卫生室旁");
        pkg.setWeight(1.2);
        pkg.setEstimatedFee(BigDecimal.ZERO);
        pkg.setStatus("ASSIGNED");
        pkg.setPickupCode("QJ25003");
        pkg.setSenderName("县医院");
        pkg.setRewardAmount(new BigDecimal("12"));
        pkg.setTimeline(Arrays.asList("县医院已发出", "站点完成入库", "张师傅正在送货上门"));
        pkg.setCourierName("张师傅");
        pkg.setStationId("ST-001");
        pkg.setStationName("清河村中心驿站");
        pkg.setLat(30.503);
        pkg.setLng(114.302);
        return pkg;
    }
}