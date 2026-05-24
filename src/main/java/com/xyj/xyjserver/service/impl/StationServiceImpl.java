package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.service.StationService;
import com.xyj.xyjserver.vo.StationVO;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class StationServiceImpl implements StationService {

    @Override
    public List<StationVO> getNearbyStations(Double lat, Double lng) {
        StationVO vo = new StationVO();
        vo.setStationId("ST-001");
        vo.setName("清河村中心驿站");
        vo.setAddress("清河村 3 组");
        vo.setPhone("13900001111");
        vo.setBusinessHours("08:00-20:00");
        vo.setLat(30.508);
        vo.setLng(114.305);
        vo.setDistance(1.2);
        return Collections.singletonList(vo);
    }

    @Override
    public StationVO getStationDetail(String stationId) {
        StationVO vo = new StationVO();
        vo.setStationId(stationId);
        vo.setName("清河村中心驿站");
        vo.setAddress("清河村 3 组");
        vo.setPhone("13900001111");
        vo.setBusinessHours("08:00-20:00");
        vo.setLat(30.508);
        vo.setLng(114.305);
        vo.setDistance(0.0);
        return vo;
    }
}