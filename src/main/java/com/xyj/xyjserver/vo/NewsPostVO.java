package com.xyj.xyjserver.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.Date;

@Data
public class NewsPostVO {
    private Long id;
    private String title;
    private String content;
    private String tag;
    
    @JsonProperty("is_urgent")
    private Boolean isUrgent;
    
    @JsonProperty("publish_time")
    private Date publishTime;
    
    private Integer likes;
    
    @JsonProperty("comments_count")
    private Integer commentsCount;
}