package com.xyj.xyjserver.entity;

import lombok.Data;
import java.util.Date;

@Data
public class User {
    private Long id;
    private String userNo;
    private String phone;
    private String email;
    private String passwordHash;
    private String nickname;
    private String avatarUrl;
    private String signature;
    private String tags;
    private Integer gender;
    private Date birthday;
    private Integer status;
    private Date lastLoginTime;
    private String lastLoginIp;
    private String deviceId;
    private Integer registerSource;
    private String registerIp;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
}