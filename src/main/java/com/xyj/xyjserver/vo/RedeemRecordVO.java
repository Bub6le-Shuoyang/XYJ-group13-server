package com.xyj.xyjserver.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.Date;

@Data
public class RedeemRecordVO {
    private Long id;
    
    @JsonProperty("item_name")
    private String itemName;
    
    @JsonProperty("points_cost")
    private Integer pointsCost;
    
    @JsonProperty("redeem_time")
    private Date redeemTime;
    
    private String status;
}