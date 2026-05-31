package com.xyj.xyjserver.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SplashAdVO {
    @JsonProperty("ad_no")
    private String adNo;
    
    private String name;
    
    @JsonProperty("image_url")
    private String imageUrl;
    
    @JsonProperty("target_url")
    private String targetUrl;
}