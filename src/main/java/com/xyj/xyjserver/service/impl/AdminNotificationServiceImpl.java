package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.entity.AdminNotification;
import com.xyj.xyjserver.mapper.AdminNotificationMapper;
import com.xyj.xyjserver.service.AdminNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminNotificationServiceImpl implements AdminNotificationService {

    @Autowired
    private AdminNotificationMapper adminNotificationMapper;

    @Override
    public PageResult<AdminNotification> getNotifications(Long page, Long size) {
        long safePage = page == null || page < 1 ? 1L : page;
        long safeSize = size == null || size < 1 ? 10L : size;
        long offset = (safePage - 1) * safeSize;
        List<AdminNotification> records = adminNotificationMapper.findAll(offset, safeSize);
        long total = adminNotificationMapper.countAll();
        return new PageResult<>(records, total, safeSize, safePage);
    }

    @Override
    public long getUnreadCount() {
        return adminNotificationMapper.countUnread();
    }

    @Override
    public void markAsRead(Long id) {
        adminNotificationMapper.markAsRead(id);
    }

    @Override
    public void markAllAsRead() {
        adminNotificationMapper.markAllAsRead();
    }

    @Override
    public void notify(String title, String content, String type) {
        AdminNotification notification = new AdminNotification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type == null ? "INFO" : type);
        adminNotificationMapper.insert(notification);
    }
}
