package com.xyj.xyjserver.vo;

import lombok.Data;
import java.util.Date;

@Data
public class CommentVO {
    private Long id;
    private String content;
    private String author;
    private Date time;
}