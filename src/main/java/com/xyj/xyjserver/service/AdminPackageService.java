package com.xyj.xyjserver.service;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.dto.PackageInboundDTO;
import com.xyj.xyjserver.dto.TaskPublishDTO;
import com.xyj.xyjserver.vo.PackageVO;
import com.xyj.xyjserver.vo.StationStatisticsVO;
import com.xyj.xyjserver.vo.TaskVO;

public interface AdminPackageService {
    PageResult<PackageVO> getStationPackages(Long adminId, String status, Long page, Long size);
    Boolean inboundPackage(Long adminId, String packageId, PackageInboundDTO inboundDTO);
    Boolean outboundPackage(Long adminId, String packageId);
    TaskVO publishTask(Long adminId, TaskPublishDTO publishDTO);
    StationStatisticsVO getStationStatistics(Long adminId);
}