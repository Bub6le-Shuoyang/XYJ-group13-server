package com.xyj.xyjserver.entity;

import lombok.Data;
import java.util.Date;

@Data
public class HelpItem {
    private Long id;
    private String helpNo;
    private String title;
    private String content;
    private Integer sortOrder;
    private Integer status;
    private Date createdAt;
}
