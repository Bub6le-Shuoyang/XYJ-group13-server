package com.xyj.xyjserver.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.crypto.digest.BCrypt;
import com.xyj.xyjserver.common.api.ResultCode;
import com.xyj.xyjserver.common.exception.BusinessException;
import com.xyj.xyjserver.common.util.JwtUtil;
import com.xyj.xyjserver.dto.LoginDTO;
import com.xyj.xyjserver.dto.RefreshTokenDTO;
import com.xyj.xyjserver.dto.RegisterDTO;
import com.xyj.xyjserver.dto.SendEmailCodeDTO;
import com.xyj.xyjserver.entity.Admin;
import com.xyj.xyjserver.entity.Courier;
import com.xyj.xyjserver.entity.User;
import com.xyj.xyjserver.mapper.AdminMapper;
import com.xyj.xyjserver.mapper.CourierMapper;
import com.xyj.xyjserver.mapper.UserMapper;
import com.xyj.xyjserver.service.AuthService;
import com.xyj.xyjserver.vo.CaptchaResponseVO;
import com.xyj.xyjserver.vo.LoginResponseVO;
import com.xyj.xyjserver.vo.UserVO;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CourierMapper courierMapper;

    // 内存缓存模拟Redis，实际项目应使用Redis，且设置过期时间
    private static final Map<String, String> CAPTCHA_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, String> EMAIL_CODE_CACHE = new ConcurrentHashMap<>();

    @Override
    public CaptchaResponseVO getCaptcha() {
        // 生成图形验证码 (宽200, 高100, 4个字符, 50条干扰线)
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 100, 4, 50);
        String code = lineCaptcha.getCode();
        String base64 = lineCaptcha.getImageBase64Data();
        
        String captchaId = "cpt_" + UUID.randomUUID().toString().replace("-", "");
        CAPTCHA_CACHE.put(captchaId, code.toLowerCase());

        CaptchaResponseVO vo = new CaptchaResponseVO();
        vo.setCaptchaId(captchaId);
        vo.setCaptchaImageBase64(base64);
        return vo;
    }

    @Override
    public Boolean sendEmailCode(SendEmailCodeDTO sendEmailCodeDTO) {
        // 1. 校验图形验证码
        String cachedCaptcha = CAPTCHA_CACHE.get(sendEmailCodeDTO.getCaptchaId());
        if (cachedCaptcha == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "图形验证码已过期或不存在");
        }
        if (!cachedCaptcha.equals(sendEmailCodeDTO.getCaptchaCode().toLowerCase())) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "图形验证码错误");
        }
        // 校验通过后清除图形验证码
        CAPTCHA_CACHE.remove(sendEmailCodeDTO.getCaptchaId());

        // 2. 生成 6 位数字验证码
        String emailCode = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        
        // 3. 将验证码存入缓存 (模拟发送)
        EMAIL_CODE_CACHE.put(sendEmailCodeDTO.getEmail(), emailCode);
        
        // 控制台打印模拟发送邮件
        System.out.println("====== [模拟发送邮件] ======");
        System.out.println("收件人: " + sendEmailCodeDTO.getEmail());
        System.out.println("验证码: " + emailCode);
        System.out.println("============================");

        return true;
    }

    @Override
    public LoginResponseVO register(RegisterDTO registerDTO) {
        // 1. 校验密码是否一致
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "两次输入的密码不一致");
        }

        // 2. 校验邮箱验证码
        String cachedCode = EMAIL_CODE_CACHE.get(registerDTO.getEmail());
        if (cachedCode == null || !cachedCode.equals(registerDTO.getEmailCode())) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "邮箱验证码错误或已过期");
        }

        // 3. 密码加密
        String passwordHash = BCrypt.hashpw(registerDTO.getPassword(), BCrypt.gensalt());
        String role = registerDTO.getRole().toUpperCase();
        
        Long userId = null;
        UserVO userVO = null;

        // 4. 根据角色插入数据
        switch (role) {
            case "ADMIN":
                if (adminMapper.findByAccount(registerDTO.getEmail()) != null) {
                    throw new BusinessException(ResultCode.VALIDATE_FAILED, "该邮箱已被注册为管理员");
                }
                Admin admin = new Admin();
                admin.setUsername(registerDTO.getEmail()); // 默认使用邮箱作为用户名
                admin.setEmail(registerDTO.getEmail());
                admin.setPasswordHash(passwordHash);
                admin.setRole(1); // 默认普通管理员
                admin.setStatus(1); // 正常状态
                adminMapper.insert(admin);
                userId = admin.getId();
                userVO = convertToUserVO(admin);
                break;

            case "USER":
                if (userMapper.findByAccount(registerDTO.getEmail()) != null) {
                    throw new BusinessException(ResultCode.VALIDATE_FAILED, "该邮箱已被注册为普通用户");
                }
                User user = new User();
                user.setUserNo("U" + System.currentTimeMillis());
                user.setEmail(registerDTO.getEmail());
                user.setPasswordHash(passwordHash);
                user.setNickname(registerDTO.getEmail().split("@")[0]); // 默认截取邮箱前缀作为昵称
                user.setStatus(1);
                userMapper.insert(user);
                userId = user.getId();
                userVO = convertToUserVO(user);
                break;

            case "COURIER":
                if (courierMapper.findByAccount(registerDTO.getEmail()) != null) {
                    throw new BusinessException(ResultCode.VALIDATE_FAILED, "该邮箱已被注册为骑手");
                }
                Courier courier = new Courier();
                courier.setCourierNo("C" + System.currentTimeMillis());
                courier.setAccount(registerDTO.getEmail()); // 骑手登录账号用邮箱
                courier.setPasswordHash(passwordHash);
                courier.setName("骑手_" + registerDTO.getEmail().split("@")[0]); // 默认名字
                courier.setStatus(1);
                courierMapper.insert(courier);
                userId = courier.getId();
                userVO = convertToUserVO(courier);
                break;

            default:
                throw new BusinessException(ResultCode.VALIDATE_FAILED, "未知的角色类型");
        }

        // 5. 注册成功后清除邮箱验证码缓存
        EMAIL_CODE_CACHE.remove(registerDTO.getEmail());

        // 6. 返回登录状态
        return buildLoginResponse(userId, role, userVO);
    }

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