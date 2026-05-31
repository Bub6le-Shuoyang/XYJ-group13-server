package com.xyj.xyjserver.service;

import com.xyj.xyjserver.dto.LoginDTO;
import com.xyj.xyjserver.dto.RefreshTokenDTO;
import com.xyj.xyjserver.dto.RegisterDTO;
import com.xyj.xyjserver.dto.SendEmailCodeDTO;
import com.xyj.xyjserver.vo.CaptchaResponseVO;
import com.xyj.xyjserver.vo.LoginResponseVO;
import com.xyj.xyjserver.vo.UserVO;

public interface AuthService {
    LoginResponseVO login(LoginDTO loginDTO);
    UserVO getCurrentUser(Long userId, String role);
    LoginResponseVO refreshToken(RefreshTokenDTO refreshTokenDTO);
    Boolean logout(Long userId);
    
    CaptchaResponseVO getCaptcha();
    Boolean sendEmailCode(SendEmailCodeDTO sendEmailCodeDTO);
    LoginResponseVO register(RegisterDTO registerDTO);
}