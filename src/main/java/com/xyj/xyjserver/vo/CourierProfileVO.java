package com.xyj.xyjserver.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CourierProfileVO {
    private Long id;
    
    @JsonProperty("courier_no")
    private String courierNo;
    
    private String name;
    private String phone;
    
    @JsonProperty("avatar_url")
    private String avatarUrl;
    
    private String status;
    
    @JsonProperty("station_id")
    private String stationId;
    
    @JsonProperty("station_name")
    private String stationName;
    
    private Double rating;
}