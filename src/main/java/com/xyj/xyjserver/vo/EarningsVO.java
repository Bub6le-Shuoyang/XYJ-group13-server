package com.xyj.xyjserver.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class EarningsVO {
    @JsonProperty("total_earnings")
    private BigDecimal totalEarnings;
    
    @JsonProperty("today_earnings")
    private BigDecimal todayEarnings;
    
    @JsonProperty("completed_tasks")
    private Integer completedTasks;
    
    @JsonProperty("today_tasks")
    private Integer todayTasks;
    
    private BigDecimal balance;
}