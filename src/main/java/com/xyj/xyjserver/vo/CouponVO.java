package com.xyj.xyjserver.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class CouponVO {
    private Long id;
    private String name;
    
    @JsonProperty("discount_amount")
    private BigDecimal discountAmount;
    
    @JsonProperty("min_spend")
    private BigDecimal minSpend;
    
    private String status;
    
    @JsonProperty("valid_until")
    private Date validUntil;
}