package com.xyj.xyjserver.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Station {
    private Long id;
    private String stationNo;
    private String name;
    private String address;
    private BigDecimal lat;
    private BigDecimal lng;
    private String phone;
    private String openingHours;
    private Integer status;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
}
