package com.xyj.xyjserver.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MallItemVO {
    private Long id;
    private String name;
    
    @JsonProperty("image_url")
    private String imageUrl;
    
    @JsonProperty("points_required")
    private Integer pointsRequired;
    
    private Integer stock;
}