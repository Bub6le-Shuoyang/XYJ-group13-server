package com.xyj.xyjserver.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class UserPointsAccount {
    private Long id;
    private Long userId;
    private Integer points;
    private Integer couponCount;
    private BigDecimal balance;
    private String memberLevel;
    private Integer monthlySignedCount;
    private Date updatedAt;
}