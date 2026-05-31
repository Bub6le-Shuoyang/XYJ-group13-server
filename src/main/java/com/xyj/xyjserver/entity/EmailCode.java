package com.xyj.xyjserver.entity;

import lombok.Data;
import java.util.Date;

@Data
public class EmailCode {
    private Long id;
    private String email;
    private String code;
    private Integer used; // 0=未使用，1=已使用
    private Date createdAt;
    private Date expireTime;
}