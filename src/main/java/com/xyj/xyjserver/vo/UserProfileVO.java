package com.xyj.xyjserver.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class UserProfileVO {
    private Long id;
    
    @JsonProperty("user_no")
    private String userNo;
    
    private String nickname;
    
    @JsonProperty("avatar_url")
    private String avatarUrl;
    
    private String phone;
    
    @JsonProperty("is_realname_auth")
    private Boolean isRealnameAuth;
    
    private BigDecimal balance;
    
    private Integer points;
    
    @JsonProperty("coupon_count")
    private Integer couponCount;
}