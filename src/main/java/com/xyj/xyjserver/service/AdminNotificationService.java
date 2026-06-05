package com.xyj.xyjserver.service;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.entity.AdminNotification;

public interface AdminNotificationService {

    PageResult<AdminNotification> getNotifications(Long page, Long size);

    long getUnreadCount();

    void markAsRead(Long id);

    void markAllAsRead();

    void notify(String title, String content, String type);
}
