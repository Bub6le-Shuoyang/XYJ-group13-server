package com.xyj.xyjserver.entity;

import lombok.Data;
import java.util.Date;

@Data
public class MallItem {
    private Long id;
    private String itemNo;
    private String name;
    private String description;
    private Integer points;
    private String type;
    private Integer stock;
    private Integer status;
    private String imageUrl;
    private Date createdAt;
    private Date updatedAt;
}