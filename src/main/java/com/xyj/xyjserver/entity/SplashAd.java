package com.xyj.xyjserver.entity;

import lombok.Data;
import java.util.Date;

@Data
public class SplashAd {
    private Long id;
    private String adNo;
    private String name;
    private String imageUrl;
    private String targetUrl;
    private Integer weight;
    private Integer status;
    private Date createdAt;
    private Date updatedAt;
}