package com.xyj.xyjserver.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.xyj.xyjserver.common.api.ResultCode;
import com.xyj.xyjserver.common.exception.BusinessException;
import com.xyj.xyjserver.common.util.JwtUtil;
import com.xyj.xyjserver.dto.LoginDTO;
import com.xyj.xyjserver.dto.RefreshTokenDTO;
import com.xyj.xyjserver.entity.Admin;
import com.xyj.xyjserver.entity.Courier;
import com.xyj.xyjserver.entity.User;
import com.xyj.xyjserver.mapper.AdminMapper;
import com.xyj.xyjserver.mapper.CourierMapper;
import com.xyj.xyjserver.mapper.UserMapper;
import com.xyj.xyjserver.service.AuthService;
import com.xyj.xyjserver.vo.LoginResponseVO;
import com.xyj.xyjserver.vo.UserVO;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CourierMapper courierMapper;

    @Override
    public LoginResponseVO login(LoginDTO loginDTO) {
        String role = loginDTO.getRole().toUpperCase();
        UserVO userVO = null;

        switch (role) {
            case "ADMIN":
                Admin admin = adminMapper.findByAccount(loginDTO.getAccount());
                if (admin == null || !BCrypt.checkpw(loginDTO.getPassword(), admin.getPasswordHash())) {
                    throw new BusinessException(ResultCode.VALIDATE_FAILED, "账号或密码错误");
                }
                if (admin.getStatus() == 0) {
                    throw new BusinessException(ResultCode.FORBIDDEN, "账号已被禁用");
                }
                userVO = convertToUserVO(admin);
                break;
            case "USER":
                User user = userMapper.findByAccount(loginDTO.getAccount());
                if (user == null || !BCrypt.checkpw(loginDTO.getPassword(), user.getPasswordHash())) {
                    throw new BusinessException(ResultCode.VALIDATE_FAILED, "账号或密码错误");
                }
                if (user.getStatus() != 1) {
                    throw new BusinessException(ResultCode.FORBIDDEN, "账号状态异常");
                }
                userVO = convertToUserVO(user);
                break;
            case "COURIER":
                Courier courier = courierMapper.findByAccount(loginDTO.getAccount());
                if (courier == null || !BCrypt.checkpw(loginDTO.getPassword(), courier.getPasswordHash())) {
                    throw new BusinessException(ResultCode.VALIDATE_FAILED, "账号或密码错误");
                }
                if (courier.getStatus() == 0) {
                    throw new BusinessException(ResultCode.FORBIDDEN, "账号已被禁用");
                }
                userVO = convertToUserVO(courier);
                break;
            default:
                throw new BusinessException(ResultCode.VALIDATE_FAILED, "未知的角色类型");
        }

        return buildLoginResponse(userVO.getId(), role, userVO);
    }

    @Override
    public UserVO getCurrentUser(Long userId, String role) {
        if (role == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "无法识别角色");
        }
        return getCurrentUserByRoleAndId(userId, role);
    }

    @Override
    public LoginResponseVO refreshToken(RefreshTokenDTO refreshTokenDTO) {
        String refreshToken = refreshTokenDTO.getRefreshToken();
        if (!JwtUtil.validateToken(refreshToken)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "无效的 refresh_token");
        }
        Claims claims = JwtUtil.parseToken(refreshToken);
        if (!"refresh".equals(claims.get("type"))) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "Token 类型不匹配");
        }

        Long userId = Long.parseLong(claims.getSubject());
        String role = claims.get("role", String.class);

        // 获取最新用户信息
        UserVO userVO = getCurrentUserByRoleAndId(userId, role);
        return buildLoginResponse(userId, role, userVO);
    }

    @Override
    public Boolean logout(Long userId) {
        // 对于 JWT 来说，通常在客户端清除 Token 即可
        // 如果需要在服务端实现真正的注销，可以将 Token 加入黑名单 (Redis)
        return true;
    }

    private LoginResponseVO buildLoginResponse(Long userId, String role, UserVO userVO) {
        String token = JwtUtil.generateToken(userId, role);
        String refreshToken = JwtUtil.generateRefreshToken(userId, role);
        
        LoginResponseVO response = new LoginResponseVO();
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(JwtUtil.EXPIRATION_TIME / 1000);
        response.setUser(userVO);
        return response;
    }

    private UserVO getCurrentUserByRoleAndId(Long userId, String role) {
        switch (role) {
            case "ADMIN":
                return convertToUserVO(adminMapper.findById(userId));
            case "VILLAGER":
                return convertToUserVO(userMapper.findById(userId));
            case "COURIER":
                return convertToUserVO(courierMapper.findById(userId));
            default:
                throw new BusinessException(ResultCode.UNAUTHORIZED, "未知的角色类型");
        }
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

    private UserVO convertToUserVO(User user) {
        if (user == null) return null;
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUserNo(user.getUserNo());
        vo.setAccount(user.getEmail() != null ? user.getEmail() : user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setNickname(user.getNickname());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setRole("VILLAGER");
        vo.setIsRealnameAuth(false);
        return vo;
    }

    private UserVO convertToUserVO(Courier courier) {
        if (courier == null) return null;
        UserVO vo = new UserVO();
        vo.setId(courier.getId());
        vo.setUserNo(courier.getCourierNo());
        vo.setAccount(courier.getAccount());
        vo.setEmail(null);
        vo.setPhone(courier.getPhone());
        vo.setNickname(courier.getName());
        vo.setAvatarUrl(courier.getAvatarUrl());
        vo.setRole("COURIER");
        vo.setIsRealnameAuth(true);
        return vo;
    }
}