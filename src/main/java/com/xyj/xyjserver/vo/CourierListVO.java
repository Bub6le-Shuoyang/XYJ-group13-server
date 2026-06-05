package com.xyj.xyjserver.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class CourierListVO {
    private Long id;

    @JsonProperty("courier_no")
    private String courierNo;

    private String account;
    private String name;
    private String phone;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    @JsonProperty("station_id")
    private Long stationId;

    @JsonProperty("station_name")
    private String stationName;

    @JsonProperty("level_name")
    private String levelName;

    private Integer status;

    @JsonProperty("total_earnings")
    private BigDecimal totalEarnings;

    @JsonProperty("completed_tasks")
    private Integer completedTasks;

    @JsonProperty("active_tasks")
    private Integer activeTasks;

    @JsonProperty("created_at")
    private Date createdAt;
}
