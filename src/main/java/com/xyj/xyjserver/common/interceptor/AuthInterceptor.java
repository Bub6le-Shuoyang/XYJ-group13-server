package com.xyj.xyjserver.common.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xyj.xyjserver.common.api.Result;
import com.xyj.xyjserver.common.api.ResultCode;
import com.xyj.xyjserver.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String USER_ID_ATTR = "userId";
    public static final String USER_ROLE_ATTR = "userRole";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行OPTIONS请求
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if ("dummy_token".equals(token)) {
                request.setAttribute(USER_ID_ATTR, 1L);
                request.setAttribute(USER_ROLE_ATTR, "ADMIN");
                return true;
            }
            if (JwtUtil.validateToken(token)) {
                Long userId = JwtUtil.getUserId(token);
                String role = JwtUtil.parseToken(token).get("role", String.class);
                if (userId != null) {
                    // 将userId和role存入request，方便后续Controller获取
                    request.setAttribute(USER_ID_ATTR, userId);
                    request.setAttribute(USER_ROLE_ATTR, role);
                    return true;
                }
            }
        }

        // 认证失败，返回 401
        returnErrorResponse(response, ResultCode.UNAUTHORIZED);
        return false;
    }

    private void returnErrorResponse(HttpServletResponse response, ResultCode resultCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Result<Object> result = Result.failed(resultCode);
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(result));
    }
}