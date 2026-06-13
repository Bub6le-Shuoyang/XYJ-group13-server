package com.xyj.xyjserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xyj.xyjserver.common.exception.GlobalExceptionHandler;
import com.xyj.xyjserver.common.util.JwtUtil;
import com.xyj.xyjserver.dto.LoginDTO;
import com.xyj.xyjserver.dto.RefreshTokenDTO;
import com.xyj.xyjserver.service.AuthService;
import com.xyj.xyjserver.vo.CaptchaResponseVO;
import com.xyj.xyjserver.vo.LoginResponseVO;
import com.xyj.xyjserver.vo.UserVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AuthService authService;

    @Test
    void getCaptcha_shouldReturnSuccessPayload() throws Exception {
        CaptchaResponseVO captcha = new CaptchaResponseVO();
        captcha.setCaptchaId("cpt_test");
        captcha.setCaptchaImageBase64("base64-data");
        when(authService.getCaptcha()).thenReturn(captcha);

        mockMvc.perform(get("/api/v1/auth/captcha"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.captchaId").value("cpt_test"));
    }

    @Test
    void login_shouldReturnTokenPayload() throws Exception {
        LoginResponseVO response = new LoginResponseVO();
        response.setToken("access-token");
        response.setRefreshToken("refresh-token");
        response.setExpiresIn(7200L);
        UserVO user = new UserVO();
        user.setId(1L);
        user.setRole("USER");
        response.setUser(user);
        when(authService.login(any(LoginDTO.class))).thenReturn(response);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setAccount("user@example.com");
        loginDTO.setPassword("Pass123!");
        loginDTO.setRole("USER");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("access-token"))
                .andExpect(jsonPath("$.data.user.role").value("USER"));
    }

    @Test
    void login_missingFields_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void refresh_shouldReturnNewTokens() throws Exception {
        LoginResponseVO response = new LoginResponseVO();
        response.setToken(JwtUtil.generateToken(1L, "USER"));
        response.setRefreshToken(JwtUtil.generateRefreshToken(1L, "USER"));
        when(authService.refreshToken(any(RefreshTokenDTO.class))).thenReturn(response);

        RefreshTokenDTO dto = new RefreshTokenDTO();
        dto.setRefreshToken("refresh-token");

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").exists());
    }

    @Test
    void logout_shouldReturnTrue() throws Exception {
        when(authService.logout(eq(1L))).thenReturn(true);
        String token = JwtUtil.generateToken(1L, "USER");

        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
    }
}
