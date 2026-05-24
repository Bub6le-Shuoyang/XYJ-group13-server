package com.xyj.xyjserver.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.Date;

@Data
public class UserListVO {
    private Long id;
    
    @JsonProperty("user_no")
    private String userNo;
    
    private String account;
    private String email;
    private String phone;
    private String nickname;
    private String role;
    
    @JsonProperty("created_at")
    private Date createdAt;
}