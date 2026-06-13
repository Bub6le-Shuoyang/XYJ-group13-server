package com.xyj.xyjserver.common.interceptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xyj.xyjserver.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthInterceptorTest {

    private AuthInterceptor interceptor;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        interceptor = new AuthInterceptor();
        objectMapper = new ObjectMapper();
    }

    @Test
    void preHandle_shouldAllowOptionsRequest() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getMethod()).thenReturn("OPTIONS");

        assertTrue(interceptor.preHandle(request, response, new Object()));
        verify(response, never()).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void preHandle_shouldAllowValidBearerToken() throws Exception {
        String token = JwtUtil.generateToken(7L, "USER");
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        assertTrue(interceptor.preHandle(request, response, new Object()));
        verify(request).setAttribute(AuthInterceptor.USER_ID_ATTR, 7L);
        verify(request).setAttribute(AuthInterceptor.USER_ROLE_ATTR, "USER");
    }

    @Test
    void preHandle_shouldRejectMissingOrInvalidToken() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter stringWriter = new StringWriter();
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter, true));

        assertFalse(interceptor.preHandle(request, response, new Object()));

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        JsonNode body = objectMapper.readTree(stringWriter.toString());
        assertEquals(401, body.get("code").asInt());
    }
}
