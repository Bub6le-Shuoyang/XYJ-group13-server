package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.common.api.ResultCode;
import com.xyj.xyjserver.common.exception.BusinessException;
import com.xyj.xyjserver.dto.CreateStationDTO;
import com.xyj.xyjserver.entity.Station;
import com.xyj.xyjserver.mapper.StationMapper;
import com.xyj.xyjserver.vo.StationListVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminStationServiceImplTest {

    @Mock
    private StationMapper stationMapper;

    @InjectMocks
    private AdminStationServiceImpl adminStationService;

    @Test
    void getStationList_shouldReturnPagedResult() {
        Station station = buildStation(1L, "ST-001", "测试驿站");
        when(stationMapper.searchByKeyword(eq("测试"), eq(0L), eq(10L))).thenReturn(List.of(station));
        when(stationMapper.countByKeyword("测试")).thenReturn(1L);

        PageResult<StationListVO> result = adminStationService.getStationList(1L, 10L, "测试");

        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals("ST-001", result.getRecords().get(0).getStationNo());
    }

    @Test
    void createStation_duplicateStationNo_shouldThrow() {
        CreateStationDTO dto = buildDto("ST-001", "重复站点");
        when(stationMapper.countByStationNoExclude("ST-001", 0L)).thenReturn(1L);

        assertThrows(BusinessException.class, () -> adminStationService.createStation(dto));
    }

    @Test
    void createStation_success_shouldInsertStation() {
        CreateStationDTO dto = buildDto("ST-002", "新站点");
        when(stationMapper.countByStationNoExclude("ST-002", 0L)).thenReturn(0L);
        doAnswer(invocation -> {
            Station station = invocation.getArgument(0);
            station.setId(10L);
            return 1;
        }).when(stationMapper).insert(any(Station.class));

        Station created = adminStationService.createStation(dto);

        assertEquals(10L, created.getId());
        assertEquals("ST-002", created.getStationNo());
        assertEquals(1, created.getStatus());
    }

    @Test
    void updateStation_notFound_shouldThrow() {
        when(stationMapper.findById(99L)).thenReturn(null);

        assertThrows(BusinessException.class,
                () -> adminStationService.updateStation(99L, buildDto("ST-003", "不存在")));
    }

    @Test
    void deleteStation_notFound_shouldThrow() {
        when(stationMapper.findById(1L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> adminStationService.deleteStation(1L));
    }

    @Test
    void deleteStation_success_shouldSoftDelete() {
        Station station = buildStation(1L, "ST-001", "待删除");
        when(stationMapper.findById(1L)).thenReturn(station);
        when(stationMapper.softDelete(1L)).thenReturn(1);

        assertDoesNotThrow(() -> adminStationService.deleteStation(1L));
        verify(stationMapper).softDelete(1L);
    }

    @Test
    void getAllActiveStations_shouldReturnList() {
        when(stationMapper.findAllActive()).thenReturn(Collections.emptyList());

        assertTrue(adminStationService.getAllActiveStations().isEmpty());
    }

    private CreateStationDTO buildDto(String stationNo, String name) {
        CreateStationDTO dto = new CreateStationDTO();
        dto.setStationNo(stationNo);
        dto.setName(name);
        dto.setAddress("测试地址");
        dto.setLat(new BigDecimal("39.9"));
        dto.setLng(new BigDecimal("116.3"));
        return dto;
    }

    private Station buildStation(Long id, String stationNo, String name) {
        Station station = new Station();
        station.setId(id);
        station.setStationNo(stationNo);
        station.setName(name);
        station.setAddress("地址");
        station.setStatus(1);
        return station;
    }
}
