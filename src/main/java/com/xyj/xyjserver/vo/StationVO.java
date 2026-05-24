package com.xyj.xyjserver.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StationVO {
    @JsonProperty("station_id")
    private String stationId;
    
    private String name;
    private String address;
    private String phone;
    
    @JsonProperty("business_hours")
    private String businessHours;
    
    private Double lat;
    private Double lng;
    private Double distance;
}