package com.xyj.xyjserver.common.config;

import com.xyj.xyjserver.common.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                // 拦截所有 /api/v1 下的请求
                .addPathPatterns("/api/v1/**")
                // 放行登录和密码重置相关的接口
                .excludePathPatterns(
                        "/api/v1/auth/login",
                        "/api/v1/admin/auth/login",
                        "/api/v1/admin/auth/reset-password",
                        "/api/v1/auth/refresh",
                        "/api/v1/admin/auth/captcha",
                        "/api/v1/admin/auth/send-email-code",
                        "/api/v1/admin/auth/register"
                );
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // 注释掉，因为 Controller 已经自带 /api/v1 前缀
        // configurer.addPathPrefix("/api/v1", c -> c.isAnnotationPresent(RestController.class));
    }
}