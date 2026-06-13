package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.entity.AdminOperationLog;
import com.xyj.xyjserver.mapper.AdminOperationLogMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminOperationLogServiceImplTest {

    @Mock
    private AdminOperationLogMapper adminOperationLogMapper;

    @InjectMocks
    private AdminOperationLogServiceImpl adminOperationLogService;

    @Test
    void log_shouldPersistOperationDetails() {
        adminOperationLogService.log(1L, "管理员", "DELETE", "STATION", 10L, "删除站点", "127.0.0.1");

        ArgumentCaptor<AdminOperationLog> captor = ArgumentCaptor.forClass(AdminOperationLog.class);
        verify(adminOperationLogMapper).insert(captor.capture());

        AdminOperationLog log = captor.getValue();
        assertEquals(1L, log.getAdminId());
        assertEquals("DELETE", log.getOperation());
        assertEquals("STATION", log.getTargetType());
        assertEquals(10L, log.getTargetId());
        assertEquals("127.0.0.1", log.getIp());
    }

    @Test
    void getLogs_shouldReturnPagedLogs() {
        AdminOperationLog log = new AdminOperationLog();
        log.setId(1L);
        log.setOperation("CREATE");
        when(adminOperationLogMapper.findByPage(0L, 20L, "CREATE")).thenReturn(List.of(log));
        when(adminOperationLogMapper.countByFilter("CREATE")).thenReturn(1L);

        PageResult<AdminOperationLog> result = adminOperationLogService.getLogs(1L, 20L, "CREATE");

        assertEquals(1L, result.getTotal());
        assertEquals("CREATE", result.getRecords().get(0).getOperation());
    }
}
