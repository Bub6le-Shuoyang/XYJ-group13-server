package com.xyj.xyjserver.vo;

import lombok.Data;

@Data
public class CaptchaResponseVO {
    private String captchaId;
    private String captchaImageBase64;
}