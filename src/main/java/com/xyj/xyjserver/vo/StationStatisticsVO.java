package com.xyj.xyjserver.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StationStatisticsVO {
    @JsonProperty("pending_inbound_count")
    private Integer pendingInboundCount;
    
    @JsonProperty("in_stock_count")
    private Integer inStockCount;
    
    @JsonProperty("delivering_count")
    private Integer deliveringCount;
    
    @JsonProperty("completed_count")
    private Integer completedCount;
    
    @JsonProperty("today_inbound")
    private Integer todayInbound;
    
    @JsonProperty("today_outbound")
    private Integer todayOutbound;
}