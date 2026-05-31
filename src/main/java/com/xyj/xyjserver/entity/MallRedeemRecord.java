package com.xyj.xyjserver.entity;

import lombok.Data;
import java.util.Date;

@Data
public class MallRedeemRecord {
    private Long id;
    private String recordNo;
    private Long userId;
    private Long itemId;
    private String itemName;
    private Integer pointsCost;
    private Integer remainPoints;
    private String status;
    private Date createdAt;
    private Date updatedAt;
}