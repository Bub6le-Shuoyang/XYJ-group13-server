package com.xyj.xyjserver.service;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.dto.CreateStationDTO;
import com.xyj.xyjserver.entity.Station;
import com.xyj.xyjserver.vo.StationListVO;

import java.util.List;

public interface AdminStationService {
    PageResult<StationListVO> getStationList(Long page, Long size, String keyword);
    Station createStation(CreateStationDTO dto);
    Station updateStation(Long id, CreateStationDTO dto);
    void deleteStation(Long id);
    List<Station> getAllActiveStations();
}
