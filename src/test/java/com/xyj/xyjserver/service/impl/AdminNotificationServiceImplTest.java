package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.entity.AdminNotification;
import com.xyj.xyjserver.mapper.AdminNotificationMapper;
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
class AdminNotificationServiceImplTest {

    @Mock
    private AdminNotificationMapper adminNotificationMapper;

    @InjectMocks
    private AdminNotificationServiceImpl adminNotificationService;

    @Test
    void getNotifications_shouldNormalizePagination() {
        when(adminNotificationMapper.findAll(0L, 10L)).thenReturn(List.of(new AdminNotification()));
        when(adminNotificationMapper.countAll()).thenReturn(1L);

        PageResult<AdminNotification> result = adminNotificationService.getNotifications(null, null);

        assertEquals(1L, result.getCurrent());
        assertEquals(10L, result.getSize());
        assertEquals(1L, result.getTotal());
    }

    @Test
    void getUnreadCount_shouldDelegateToMapper() {
        when(adminNotificationMapper.countUnread()).thenReturn(3L);

        assertEquals(3L, adminNotificationService.getUnreadCount());
    }

    @Test
    void notify_nullType_shouldDefaultToInfo() {
        adminNotificationService.notify("标题", "内容", null);

        ArgumentCaptor<AdminNotification> captor = ArgumentCaptor.forClass(AdminNotification.class);
        verify(adminNotificationMapper).insert(captor.capture());
        assertEquals("INFO", captor.getValue().getType());
        assertEquals("标题", captor.getValue().getTitle());
    }

    @Test
    void markAsRead_shouldCallMapper() {
        adminNotificationService.markAsRead(5L);
        verify(adminNotificationMapper).markAsRead(5L);
    }

    @Test
    void markAllAsRead_shouldCallMapper() {
        adminNotificationService.markAllAsRead();
        verify(adminNotificationMapper).markAllAsRead();
    }
}
