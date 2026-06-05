package com.xyj.xyjserver.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class StationListVO {
    private Long id;

    @JsonProperty("station_no")
    private String stationNo;

    private String name;
    private String address;
    private String phone;

    @JsonProperty("opening_hours")
    private String openingHours;

    private BigDecimal lat;
    private BigDecimal lng;
    private Integer status;

    @JsonProperty("created_at")
    private Date createdAt;
}
