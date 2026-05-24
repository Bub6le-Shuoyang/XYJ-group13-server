package com.xyj.xyjserver.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserVO {
    private Long id;
    
    @JsonProperty("user_no")
    private String userNo;
    
    private String account;
    private String email;
    private String phone;
    private String nickname;
    
    @JsonProperty("avatar_url")
    private String avatarUrl;
    
    private String role;
    
    @JsonProperty("is_realname_auth")
    private Boolean isRealnameAuth;
}