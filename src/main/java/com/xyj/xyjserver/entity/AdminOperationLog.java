package com.xyj.xyjserver.entity;

import lombok.Data;
import java.util.Date;

@Data
public class AdminOperationLog {
    private Long id;
    private Long adminId;
    private String adminName;
    private String operation;
    private String targetType;
    private Long targetId;
    private String detail;
    private String ip;
    private Date createdAt;
}
