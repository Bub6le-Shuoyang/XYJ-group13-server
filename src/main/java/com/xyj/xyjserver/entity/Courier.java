package com.xyj.xyjserver.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Courier {
    private Long id;
    private String courierNo;
    private String account;
    private String passwordHash;
    private String name;
    private String phone;
    private String avatarUrl;
    private Long stationId;
    private String levelName;
    private BigDecimal levelProgress;
    private Integer monthlyRank;
    private Integer status;
    private Date createdAt;
    private Date updatedAt;
}