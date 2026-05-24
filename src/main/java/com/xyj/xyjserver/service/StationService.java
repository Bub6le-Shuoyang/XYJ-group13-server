package com.xyj.xyjserver.service;

import com.xyj.xyjserver.vo.StationVO;
import java.util.List;

public interface StationService {
    List<StationVO> getNearbyStations(Double lat, Double lng);
    StationVO getStationDetail(String stationId);
}