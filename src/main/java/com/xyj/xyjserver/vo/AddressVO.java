package com.xyj.xyjserver.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AddressVO {
    private Long id;
    private String name;
    private String phone;
    private String address;
    
    @JsonProperty("is_default")
    private Boolean isDefault;
}