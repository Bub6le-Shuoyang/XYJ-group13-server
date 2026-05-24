package com.xyj.xyjserver.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.xyj.xyjserver.common.api.ResultCode;
import com.xyj.xyjserver.common.exception.BusinessException;
import com.xyj.xyjserver.common.util.JwtUtil;
import com.xyj.xyjserver.dto.AdminLoginDTO;
import com.xyj.xyjserver.dto.AdminRegisterDTO;
import com.xyj.xyjserver.dto.AdminResetPasswordDTO;
import com.xyj.xyjserver.dto.SendEmailCodeDTO;
import com.xyj.xyjserver.entity.Admin;
import com.xyj.xyjserver.mapper.AdminMapper;
import com.xyj.xyjserver.service.AdminAuthService;
import com.xyj.xyjserver.vo.CaptchaResponseVO;
import com.xyj.xyjserver.vo.LoginResponseVO;
import com.xyj.xyjserver.vo.SendCaptchaResponseVO;
import com.xyj.xyjserver.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminAuthServiceImpl implements AdminAuthService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public LoginResponseVO login(AdminLoginDTO loginDTO) {
        Admin admin = adminMapper.findByAccount(loginDTO.getEmail());
        if (admin == null || !BCrypt.checkpw(loginDTO.getPassword(), admin.getPasswordHash())) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "账号或密码错误");
        }
        if (admin.getStatus() == 0) {
            throw new BusinessException(ResultCode.FORBIDDEN, "账号已被禁用");
        }

        UserVO userVO = convertToUserVO(admin);
        
        String token = JwtUtil.generateToken(admin.getId(), "ADMIN");
        String refreshToken = JwtUtil.generateRefreshToken(admin.getId(), "ADMIN");
        
        LoginResponseVO response = new LoginResponseVO();
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(JwtUtil.EXPIRATION_TIME / 1000);
        response.setUser(userVO);
        return response;
    }

    @Override
    public void resetPassword(AdminResetPasswordDTO resetPasswordDTO) {
        Admin admin = adminMapper.findByAccount(resetPasswordDTO.getEmail());
        if (admin == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "未找到该邮箱对应的管理员账号");
        }

        String newHash = BCrypt.hashpw(resetPasswordDTO.getNewPassword(), BCrypt.gensalt());
        int rows = adminMapper.updatePasswordByEmail(resetPasswordDTO.getEmail(), newHash);
        if (rows == 0) {
            throw new BusinessException(ResultCode.FAILED, "重置密码失败");
        }
    }

    @Override
    public CaptchaResponseVO getCaptcha() {
        CaptchaResponseVO vo = new CaptchaResponseVO();
        vo.setCaptchaId("mock-captcha-id");
        vo.setCaptchaImageBase64(cn.hutool.crypto.digest.BCrypt.hashpw("MyPass123!", cn.hutool.crypto.digest.BCrypt.gensalt()));
        return vo;
    }

    @Override
    public SendCaptchaResponseVO sendEmailCode(SendEmailCodeDTO sendEmailCodeDTO) {
        SendCaptchaResponseVO vo = new SendCaptchaResponseVO();
        vo.setMessage("发送成功");
        return vo;
    }

    @Override
    public LoginResponseVO register(AdminRegisterDTO adminRegisterDTO) {
        // mock implementation
        LoginResponseVO vo = new LoginResponseVO();
        vo.setToken("mock-token");
        vo.setRefreshToken("mock-refresh-token");
        vo.setExpiresIn(7200L);
        return vo;
    }

    private UserVO convertToUserVO(Admin admin) {
        if (admin == null) return null;
        UserVO vo = new UserVO();
        vo.setId(admin.getId());
        vo.setUserNo("A" + admin.getId());
        vo.setAccount(admin.getUsername());
        vo.setEmail(admin.getEmail());
        vo.setPhone(admin.getPhone());
        vo.setNickname(admin.getRealName() != null ? admin.getRealName() : admin.getUsername());
        vo.setAvatarUrl(admin.getAvatarUrl());
        vo.setRole("ADMIN");
        vo.setIsRealnameAuth(admin.getRealName() != null);
        return vo;
    }
}