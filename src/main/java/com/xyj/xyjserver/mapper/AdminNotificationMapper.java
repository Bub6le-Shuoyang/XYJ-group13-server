package com.xyj.xyjserver.mapper;

import com.xyj.xyjserver.entity.AdminNotification;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdminNotificationMapper {

    @Insert("INSERT INTO admin_notifications(title, content, type, created_at) VALUES(#{title}, #{content}, #{type}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AdminNotification n);

    @Select("SELECT * FROM admin_notifications ORDER BY created_at DESC LIMIT #{offset}, #{size}")
    List<AdminNotification> findAll(@Param("offset") long offset, @Param("size") long size);

    @Select("SELECT COUNT(*) FROM admin_notifications")
    long countAll();

    @Select("SELECT COUNT(*) FROM admin_notifications WHERE is_read = 0")
    long countUnread();

    @Update("UPDATE admin_notifications SET is_read = 1 WHERE id = #{id}")
    int markAsRead(@Param("id") Long id);

    @Update("UPDATE admin_notifications SET is_read = 1 WHERE is_read = 0")
    int markAllAsRead();
}
