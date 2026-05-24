package com.xyj.xyjserver.service;

import com.xyj.xyjserver.dto.AdminLoginDTO;
import com.xyj.xyjserver.dto.AdminRegisterDTO;
import com.xyj.xyjserver.dto.AdminResetPasswordDTO;
import com.xyj.xyjserver.dto.SendEmailCodeDTO;
import com.xyj.xyjserver.vo.CaptchaResponseVO;
import com.xyj.xyjserver.vo.LoginResponseVO;
import com.xyj.xyjserver.vo.SendCaptchaResponseVO;

public interface AdminAuthService {
    LoginResponseVO login(AdminLoginDTO loginDTO);
    void resetPassword(AdminResetPasswordDTO resetPasswordDTO);
    CaptchaResponseVO getCaptcha();
    SendCaptchaResponseVO sendEmailCode(SendEmailCodeDTO sendEmailCodeDTO);
    LoginResponseVO register(AdminRegisterDTO adminRegisterDTO);
}