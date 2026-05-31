package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.mapper.StationMapper;
import com.xyj.xyjserver.service.StationService;
import com.xyj.xyjserver.vo.StationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationServiceImpl implements StationService {

    @Autowired
    private StationMapper stationMapper;

    @Override
    public List<StationVO> getNearbyStations(Double lat, Double lng) {
        return stationMapper.findNearbyStations(lat, lng);
    }

    @Override
    public StationVO getStationDetail(String stationId) {
        return stationMapper.findStationDetail(stationId);
    }
}
