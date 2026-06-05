package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.common.api.ResultCode;
import com.xyj.xyjserver.common.exception.BusinessException;
import com.xyj.xyjserver.dto.CreateStationDTO;
import com.xyj.xyjserver.entity.Station;
import com.xyj.xyjserver.mapper.StationMapper;
import com.xyj.xyjserver.service.AdminStationService;
import com.xyj.xyjserver.vo.StationListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminStationServiceImpl implements AdminStationService {

    @Autowired
    private StationMapper stationMapper;

    @Override
    public PageResult<StationListVO> getStationList(Long page, Long size, String keyword) {
        long offset = (page - 1) * size;
        List<Station> list = stationMapper.searchByKeyword(keyword, offset, size);
        long total = stationMapper.countByKeyword(keyword);
        List<StationListVO> voList = list.stream().map(this::toVO).collect(Collectors.toList());
        return new PageResult<>(voList, total, size, page);
    }

    @Override
    public Station createStation(CreateStationDTO dto) {
        long count = stationMapper.countByStationNoExclude(dto.getStationNo(), 0L);
        if (count > 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "站点编号已存在");
        }
        Station station = new Station();
        station.setStationNo(dto.getStationNo());
        station.setName(dto.getName());
        station.setAddress(dto.getAddress());
        station.setLat(dto.getLat());
        station.setLng(dto.getLng());
        station.setPhone(dto.getPhone());
        station.setOpeningHours(dto.getOpeningHours());
        station.setStatus(1);
        stationMapper.insert(station);
        return station;
    }

    @Override
    public Station updateStation(Long id, CreateStationDTO dto) {
        Station existing = stationMapper.findById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "站点不存在");
        }
        if (dto.getStationNo() != null && !dto.getStationNo().equals(existing.getStationNo())) {
            long count = stationMapper.countByStationNoExclude(dto.getStationNo(), id);
            if (count > 0) {
                throw new BusinessException(ResultCode.VALIDATE_FAILED, "站点编号已被使用");
            }
        }
        existing.setStationNo(dto.getStationNo());
        existing.setName(dto.getName());
        existing.setAddress(dto.getAddress());
        existing.setLat(dto.getLat());
        existing.setLng(dto.getLng());
        existing.setPhone(dto.getPhone());
        existing.setOpeningHours(dto.getOpeningHours());
        stationMapper.update(existing);
        return stationMapper.findById(id);
    }

    @Override
    public void deleteStation(Long id) {
        Station existing = stationMapper.findById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "站点不存在");
        }
        int rows = stationMapper.softDelete(id);
        if (rows == 0) {
            throw new BusinessException(ResultCode.FAILED, "删除失败");
        }
    }

    @Override
    public List<Station> getAllActiveStations() {
        return stationMapper.findAllActive();
    }

    private StationListVO toVO(Station s) {
        StationListVO vo = new StationListVO();
        vo.setId(s.getId());
        vo.setStationNo(s.getStationNo());
        vo.setName(s.getName());
        vo.setAddress(s.getAddress());
        vo.setPhone(s.getPhone());
        vo.setOpeningHours(s.getOpeningHours());
        vo.setLat(s.getLat());
        vo.setLng(s.getLng());
        vo.setStatus(s.getStatus());
        vo.setCreatedAt(s.getCreatedAt());
        return vo;
    }
}
