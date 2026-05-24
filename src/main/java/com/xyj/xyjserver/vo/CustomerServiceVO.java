package com.xyj.xyjserver.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CustomerServiceVO {
    private String phone;
    
    @JsonProperty("work_time")
    private String workTime;
    
    @JsonProperty("wechat_id")
    private String wechatId;
}