package com.xyj.xyjserver.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.xyj.xyjserver.common.api.ResultCode;
import com.xyj.xyjserver.common.exception.BusinessException;
import com.xyj.xyjserver.common.util.JwtUtil;
import com.xyj.xyjserver.dto.AdminLoginDTO;
import com.xyj.xyjserver.dto.AdminResetPasswordDTO;
import com.xyj.xyjserver.entity.Admin;
import com.xyj.xyjserver.mapper.AdminMapper;
import com.xyj.xyjserver.vo.LoginResponseVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminAuthServiceImplTest {

    @Mock
    private AdminMapper adminMapper;

    @InjectMocks
    private AdminAuthServiceImpl adminAuthService;

    @Test
    void login_nonSuperAdminEmail_shouldThrowForbidden() {
        AdminLoginDTO dto = new AdminLoginDTO();
        dto.setEmail("other@example.com");
        dto.setPassword("pass");

        BusinessException ex = assertThrows(BusinessException.class, () -> adminAuthService.login(dto));
        assertEquals(ResultCode.FORBIDDEN.getCode(), ex.getErrorCode().getCode());
        assertTrue(ex.getMessage().contains("超级管理员"));
    }

    @Test
    void login_success_shouldReturnJwtTokens() {
        String password = "AdminPass123!";
        Admin admin = new Admin();
        admin.setId(1L);
        admin.setUsername("admin");
        admin.setEmail("admin");
        admin.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
        admin.setStatus(1);
        when(adminMapper.findByAccount("admin")).thenReturn(admin);

        AdminLoginDTO dto = new AdminLoginDTO();
        dto.setEmail("admin");
        dto.setPassword(password);

        LoginResponseVO response = adminAuthService.login(dto);

        assertNotNull(response.getToken());
        assertTrue(JwtUtil.validateToken(response.getToken()));
        assertEquals("ADMIN", response.getUser().getRole());
    }

    @Test
    void login_wrongPassword_shouldThrow() {
        Admin admin = new Admin();
        admin.setId(1L);
        admin.setUsername("admin");
        admin.setPasswordHash(BCrypt.hashpw("correct", BCrypt.gensalt()));
        admin.setStatus(1);
        when(adminMapper.findByAccount("admin")).thenReturn(admin);

        AdminLoginDTO dto = new AdminLoginDTO();
        dto.setEmail("admin");
        dto.setPassword("wrong");

        assertThrows(BusinessException.class, () -> adminAuthService.login(dto));
    }

    @Test
    void resetPassword_adminNotFound_shouldThrow() {
        AdminResetPasswordDTO dto = new AdminResetPasswordDTO();
        dto.setEmail("missing@example.com");
        dto.setNewPassword("NewPass123!");
        when(adminMapper.findByAccount("missing@example.com")).thenReturn(null);

        assertThrows(BusinessException.class, () -> adminAuthService.resetPassword(dto));
    }

    @Test
    void resetPassword_success_shouldUpdateHash() {
        Admin admin = new Admin();
        admin.setId(1L);
        admin.setEmail("admin@example.com");
        when(adminMapper.findByAccount("admin@example.com")).thenReturn(admin);
        when(adminMapper.updatePasswordByEmail(eq("admin@example.com"), anyString())).thenReturn(1);

        AdminResetPasswordDTO dto = new AdminResetPasswordDTO();
        dto.setEmail("admin@example.com");
        dto.setNewPassword("NewPass123!");

        assertDoesNotThrow(() -> adminAuthService.resetPassword(dto));
        verify(adminMapper).updatePasswordByEmail(eq("admin@example.com"), anyString());
    }

    @Test
    void getCaptcha_shouldReturnMockData() {
        assertNotNull(adminAuthService.getCaptcha().getCaptchaId());
        assertNotNull(adminAuthService.getCaptcha().getCaptchaImageBase64());
    }
}
