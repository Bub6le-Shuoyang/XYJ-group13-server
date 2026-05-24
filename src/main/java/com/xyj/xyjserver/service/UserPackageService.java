package com.xyj.xyjserver.service;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.dto.PackageComplainDTO;
import com.xyj.xyjserver.dto.PackageRateDTO;
import com.xyj.xyjserver.vo.PackageVO;

public interface UserPackageService {
    PageResult<PackageVO> getMyReceivePackages(Long userId, String type, Long page, Long size);
    PackageVO getPackageDetail(Long userId, String packageId);
    Boolean confirmReceipt(Long userId, String packageId);
    Boolean ratePackage(Long userId, String packageId, PackageRateDTO rateDTO);
    Boolean complainPackage(Long userId, String packageId, PackageComplainDTO complainDTO);
}