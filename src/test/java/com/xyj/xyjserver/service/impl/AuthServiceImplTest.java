package com.xyj.xyjserver.service.impl;

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
import com.xyj.xyjserver.entity.EmailCode;
import com.xyj.xyjserver.entity.User;
import com.xyj.xyjserver.mapper.AdminMapper;
import com.xyj.xyjserver.mapper.CourierMapper;
import com.xyj.xyjserver.mapper.EmailCodeMapper;
import com.xyj.xyjserver.mapper.StationMapper;
import com.xyj.xyjserver.mapper.UserMapper;
import com.xyj.xyjserver.vo.CaptchaResponseVO;
import com.xyj.xyjserver.vo.LoginResponseVO;
import com.xyj.xyjserver.vo.UserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AdminMapper adminMapper;
    @Mock
    private UserMapper userMapper;
    @Mock
    private CourierMapper courierMapper;
    @Mock
    private EmailCodeMapper emailCodeMapper;
    @Mock
    private StationMapper stationMapper;
    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "fromEmail", "test@example.com");
    }

    @Test
    void getCaptcha_shouldReturnCaptchaIdAndImage() {
        CaptchaResponseVO captcha = authService.getCaptcha();

        assertNotNull(captcha.getCaptchaId());
        assertTrue(captcha.getCaptchaId().startsWith("cpt_"));
        assertNotNull(captcha.getCaptchaImageBase64());
    }

    @Test
    void sendEmailCode_shouldRejectInvalidCaptcha() {
        SendEmailCodeDTO dto = new SendEmailCodeDTO();
        dto.setCaptchaId("missing-id");
        dto.setCaptchaCode("abcd");
        dto.setEmail("user@example.com");

        BusinessException ex = assertThrows(BusinessException.class, () -> authService.sendEmailCode(dto));
        assertEquals(ResultCode.VALIDATE_FAILED.getCode(), ex.getErrorCode().getCode());
        assertTrue(ex.getMessage().contains("图形验证码"));
    }

    @Test
    void login_adminSuccess_shouldReturnToken() {
        String password = "MyPass123!";
        Admin admin = buildAdmin(1L, "admin@example.com", BCrypt.hashpw(password, BCrypt.gensalt()), 1);
        when(adminMapper.findByAccount("admin@example.com")).thenReturn(admin);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setAccount("admin@example.com");
        loginDTO.setPassword(password);
        loginDTO.setRole("ADMIN");

        LoginResponseVO response = authService.login(loginDTO);

        assertNotNull(response.getToken());
        assertNotNull(response.getRefreshToken());
        assertEquals(7200L, response.getExpiresIn());
        assertEquals("ADMIN", response.getUser().getRole());
        assertTrue(JwtUtil.validateToken(response.getToken()));
    }

    @Test
    void login_wrongPassword_shouldThrow() {
        Admin admin = buildAdmin(1L, "admin@example.com", BCrypt.hashpw("correct", BCrypt.gensalt()), 1);
        when(adminMapper.findByAccount("admin@example.com")).thenReturn(admin);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setAccount("admin@example.com");
        loginDTO.setPassword("wrong");
        loginDTO.setRole("ADMIN");

        BusinessException ex = assertThrows(BusinessException.class, () -> authService.login(loginDTO));
        assertEquals(ResultCode.VALIDATE_FAILED.getCode(), ex.getErrorCode().getCode());
    }

    @Test
    void login_disabledAdmin_shouldThrowForbidden() {
        Admin admin = buildAdmin(1L, "admin@example.com", BCrypt.hashpw("MyPass123!", BCrypt.gensalt()), 0);
        when(adminMapper.findByAccount("admin@example.com")).thenReturn(admin);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setAccount("admin@example.com");
        loginDTO.setPassword("MyPass123!");
        loginDTO.setRole("ADMIN");

        BusinessException ex = assertThrows(BusinessException.class, () -> authService.login(loginDTO));
        assertEquals(ResultCode.FORBIDDEN.getCode(), ex.getErrorCode().getCode());
    }

    @Test
    void login_userSuccess_shouldReturnUserProfile() {
        String password = "UserPass123!";
        User user = buildUser(2L, "user@example.com", BCrypt.hashpw(password, BCrypt.gensalt()), 1);
        when(userMapper.findByAccount("user@example.com")).thenReturn(user);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setAccount("user@example.com");
        loginDTO.setPassword(password);
        loginDTO.setRole("USER");

        LoginResponseVO response = authService.login(loginDTO);

        assertEquals("USER", response.getUser().getRole());
        assertEquals("user@example.com", response.getUser().getAccount());
    }

    @Test
    void login_unknownRole_shouldThrow() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setAccount("x@example.com");
        loginDTO.setPassword("pass");
        loginDTO.setRole("UNKNOWN");

        assertThrows(BusinessException.class, () -> authService.login(loginDTO));
    }

    @Test
    void register_passwordMismatch_shouldThrow() {
        RegisterDTO dto = new RegisterDTO();
        dto.setPassword("abc123");
        dto.setConfirmPassword("abc124");

        BusinessException ex = assertThrows(BusinessException.class, () -> authService.register(dto));
        assertTrue(ex.getMessage().contains("密码不一致"));
    }

    @Test
    void register_invalidEmailCode_shouldThrow() {
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail("new@example.com");
        dto.setPassword("Pass123!");
        dto.setConfirmPassword("Pass123!");
        dto.setEmailCode("000000");
        dto.setRole("USER");
        when(emailCodeMapper.findValidCodeByEmail("new@example.com")).thenReturn(null);

        assertThrows(BusinessException.class, () -> authService.register(dto));
    }

    @Test
    void register_userSuccess_shouldCreateAccountAndReturnToken() {
        EmailCode emailCode = new EmailCode();
        emailCode.setId(10L);
        emailCode.setCode("123456");
        when(emailCodeMapper.findValidCodeByEmail("new@example.com")).thenReturn(emailCode);
        when(userMapper.findByAccount("new@example.com")).thenReturn(null);
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(100L);
            return 1;
        }).when(userMapper).insert(any(User.class));

        RegisterDTO dto = new RegisterDTO();
        dto.setEmail("new@example.com");
        dto.setPassword("Pass123!");
        dto.setConfirmPassword("Pass123!");
        dto.setEmailCode("123456");
        dto.setRole("USER");

        LoginResponseVO response = authService.register(dto);

        verify(emailCodeMapper).markAsUsed(10L);
        assertNotNull(response.getToken());
        assertEquals("USER", response.getUser().getRole());
    }

    @Test
    void refreshToken_invalidToken_shouldThrowUnauthorized() {
        RefreshTokenDTO dto = new RefreshTokenDTO();
        dto.setRefreshToken("invalid-token");

        BusinessException ex = assertThrows(BusinessException.class, () -> authService.refreshToken(dto));
        assertEquals(ResultCode.UNAUTHORIZED.getCode(), ex.getErrorCode().getCode());
    }

    @Test
    void refreshToken_accessTokenInsteadOfRefresh_shouldThrow() {
        RefreshTokenDTO dto = new RefreshTokenDTO();
        dto.setRefreshToken(JwtUtil.generateToken(1L, "USER"));

        BusinessException ex = assertThrows(BusinessException.class, () -> authService.refreshToken(dto));
        assertTrue(ex.getMessage().contains("Token 类型不匹配"));
    }

    @Test
    void refreshToken_validRefreshToken_shouldReturnNewTokens() {
        User user = buildUser(5L, "user@example.com", "hash", 1);
        when(userMapper.findById(5L)).thenReturn(user);

        RefreshTokenDTO dto = new RefreshTokenDTO();
        dto.setRefreshToken(JwtUtil.generateRefreshToken(5L, "USER"));

        LoginResponseVO response = authService.refreshToken(dto);

        assertNotNull(response.getToken());
        assertNotNull(response.getRefreshToken());
        assertEquals("USER", response.getUser().getRole());
    }

    @Test
    void getCurrentUser_nullRole_shouldThrow() {
        assertThrows(BusinessException.class, () -> authService.getCurrentUser(1L, null));
    }

    @Test
    void getCurrentUser_admin_shouldReturnProfile() {
        Admin admin = buildAdmin(3L, "admin@test.com", "hash", 1);
        when(adminMapper.findById(3L)).thenReturn(admin);

        UserVO userVO = authService.getCurrentUser(3L, "ADMIN");

        assertEquals("ADMIN", userVO.getRole());
        assertEquals("admin@test.com", userVO.getAccount());
    }

    @Test
    void logout_shouldReturnTrue() {
        assertTrue(authService.logout(1L));
    }

    private Admin buildAdmin(Long id, String email, String passwordHash, int status) {
        Admin admin = new Admin();
        admin.setId(id);
        admin.setUsername(email);
        admin.setEmail(email);
        admin.setPasswordHash(passwordHash);
        admin.setStatus(status);
        return admin;
    }

    private User buildUser(Long id, String email, String passwordHash, int status) {
        User user = new User();
        user.setId(id);
        user.setUserNo("U" + id);
        user.setEmail(email);
        user.setPasswordHash(passwordHash);
        user.setNickname("tester");
        user.setStatus(status);
        return user;
    }
}
