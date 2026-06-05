package com.xyj.xyjserver.entity;

import lombok.Data;
import java.util.Date;

@Data
public class CustomerServiceConfig {
    private Long id;
    private String phone;
    private String onlineTime;
    private String wechat;
    private Integer status;
    private Date createdAt;
    private Date updatedAt;
}
