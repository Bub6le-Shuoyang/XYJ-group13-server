package com.xyj.xyjserver.entity;

import lombok.Data;
import java.util.Date;

@Data
public class AdminNotification {
    private Long id;
    private String title;
    private String content;
    private String type;
    private Integer isRead;
    private Date createdAt;
}
