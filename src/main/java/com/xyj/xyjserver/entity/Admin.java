package com.xyj.xyjserver.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Admin {
    private Long id;
    private String username;
    private String passwordHash;
    private String realName;
    private String avatarUrl;
    private String email;
    private String phone;
    private Integer role;
    private Long stationId;
    private Integer status;
    private Date lastLoginTime;
    private String lastLoginIp;
    private Date createdAt;
    private Date updatedAt;
}